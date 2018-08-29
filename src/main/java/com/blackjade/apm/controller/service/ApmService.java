package com.blackjade.apm.controller.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.blackjade.apm.apis.CDCancel;
import com.blackjade.apm.apis.CDCancelAns;
import com.blackjade.apm.apis.CDeal;
import com.blackjade.apm.apis.CDealAns;
import com.blackjade.apm.apis.CDepositAcc;
import com.blackjade.apm.apis.CDepositAccAns;
import com.blackjade.apm.apis.CPCancel;
import com.blackjade.apm.apis.CPCancelAns;
import com.blackjade.apm.apis.CPayConfirm;
import com.blackjade.apm.apis.CPayConfirmAns;
import com.blackjade.apm.apis.CPublish;
import com.blackjade.apm.apis.CPublishAns;
import com.blackjade.apm.apis.CWithdrawAcc;
import com.blackjade.apm.apis.CWithdrawAccAns;
import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.dao.AccDao;
import com.blackjade.apm.dao.OrdDao;
import com.blackjade.apm.domain.AccRow;
import com.blackjade.apm.domain.OrdRow;
import com.blackjade.apm.exception.CapiException;

@Transactional
@Component
public class ApmService {

	@Autowired
	private AccDao acc;
	
	@Autowired
	private OrdDao ord;
	
	@Autowired
	private RestTemplate rest;

	private String url;

	//private String port;

	@PostConstruct
	public void apmInit() throws Exception {
		//this.port = "8112";
		//this.url = "http://localhost:" + port;
		this.url = "http://otc-pub/";
		//this.rest = new RestTemplate();
	}

	public CPublishAns publishApm(CPublish pub, CPublishAns ans) throws Exception {

		
		if('S'==pub.getSide()) {
				
			// lock APM for update
			AccRow accrow = null;
			try {
				accrow = this.acc.selectAccRow(pub.getClientid(), pub.getPnsgid(), pub.getPnsid());
				if (accrow == null) {
					ans.setStatus(ComStatus.PublishStatus.ACC_DB_EMPTY);
					return ans;
				}
			} catch (Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.PublishStatus.ACC_DB_MISS);
				return ans;
			}
	
			// check if APM allow
			long margin = accrow.getMargin();
			long freemargin = accrow.getFreemargin();
	
			if ((freemargin < 0) || (margin < 0)) {
				ans.setStatus(ComStatus.PublishStatus.DATABASE_ERR);
				return ans;
			}
	
			if (pub.getQuant() < 0) {			
				ans.setStatus(ComStatus.PublishStatus.IN_MSG_ERR);
				return ans;
			}
	
			if (freemargin < pub.getQuant()) {
				ans.setStatus(ComStatus.PublishStatus.ACC_QUANT_EXCEED);
				return ans;
			}
	
			// process logic
			margin += pub.getQuant();
			freemargin -= pub.getQuant();
	
			// update for APM
			int retcode = 0;
			try {
				retcode = this.acc.updatePubAccRow(pub.getClientid(), pub.getPnsgid(), pub.getPnsid(), margin, freemargin);
				if (retcode == 0) {
					ans.setStatus(ComStatus.PublishStatus.ACC_DB_MISS);
					return ans;
				}
			} catch (Exception e) {
				e.printStackTrace();				
				throw new CapiException(ComStatus.PublishStatus.ACC_DB_ERR.toString());
			}
		}else {
			if('B'!=pub.getSide()) 
			{
				ans.setStatus(ComStatus.PublishStatus.IN_MSG_ERR);
				return ans;
			}				
		}
		
		// update PnS
		
		CPublishAns pubans = null;
		try {
			pubans = this.rest.postForObject(this.url+"/publish", pub, CPublishAns.class);
			if (pubans == null) {
				throw new CapiException(ComStatus.PublishStatus.PUB_FAILED.toString());
			}

			// >> check return ans >>
			if (pubans.getStatus() != ComStatus.PublishStatus.SUCCESS) {
				throw new CapiException(pubans.getStatus().toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new CapiException(ComStatus.PublishStatus.PUB_FAILED.toString());
		}

		// success or errors will all be returned
		return pubans;
	}

	public CDealAns dealApm(CDeal deal, CDealAns ans) throws Exception {
		
		AccRow accrow = null;		
		try {
			accrow = this.acc.selectAccRow(deal.getClientid(), deal.getPnsgid(), deal.getPnsid());
			if (accrow == null) {
				ans.setStatus(ComStatus.DealStatus.ACC_DB_EMPTY);
				return ans;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.DealStatus.ACC_DB_MISS);
			return ans; 			
		}
		
		// check if acc corrupted
		if(accrow.getMargin()<0) {
			ans.setStatus(ComStatus.DealStatus.ACC_DB_CORRUPT);
			return ans;
		}
		
		if(accrow.getFreemargin()<0) {
			ans.setStatus(ComStatus.DealStatus.ACC_DB_CORRUPT);
			return ans;
		}
		
		// check balance
		if(accrow.getBalance()!=(accrow.getMargin()+accrow.getFreemargin())) {
			ans.setStatus(ComStatus.DealStatus.ACC_DB_CORRUPT);
			return ans;
		}
		
		if(accrow.getBalance()!=(accrow.getPrebalan()+accrow.getChangebalan()+accrow.getPnl())) {
			ans.setStatus(ComStatus.DealStatus.ACC_DB_CORRUPT);
			return ans;
		}			
		
		// check if deal logic 		
		if('S'==deal.getSide()) {
			
			if(accrow.getFreemargin()<deal.getQuant()) {
				ans.setStatus(ComStatus.DealStatus.IN_QUANT_ERR);
				return ans;
			}			
		
			// update ACC 
			long margin = accrow.getMargin();
			long freemargin = accrow.getFreemargin();
			
			margin += deal.getQuant();
			freemargin-= deal.getQuant();
			int retcode = 0;
			try {
				retcode = this.acc.updatePubAccRow(deal.getClientid(), deal.getPnsgid(), deal.getPnsid(), margin, freemargin);
				if(retcode==0) {
					ans.setStatus(ComStatus.DealStatus.ACC_DB_MISS);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.DealStatus.ACC_DB_MISS);
				return ans;
			}
			
		}// if it is 'B' then no check ACC
		
		// send to publisher
		CDealAns dealans = null;
		try {
			dealans = this.rest.postForObject(this.url+"/deal", deal, CDealAns.class);
			if (dealans == null) {
				ans.setStatus(ComStatus.DealStatus.PUB_FAILED);
				return ans;
			}

			// >> check return ans >>
			if (ComStatus.DealStatus.SUCCESS!=dealans.getStatus()) {
				throw new CapiException(dealans.getStatus().toString());
			}
			
		}
		catch(CapiException e) {
			throw new CapiException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CapiException(ComStatus.DealStatus.PUB_FAILED.toString());//exception handling
		}
		
		// now it is success
		return dealans;
	} 

	public CPayConfirmAns payconfirm(CPayConfirm paycon, CPayConfirmAns ans) throws Exception{
		
		// send things to payconfirm
		CPayConfirmAns payconans = null;
		try {
			payconans = this.rest.postForObject(this.url+"/payconfirm", paycon, CPayConfirmAns.class);
			if (payconans == null) {
				ans.setStatus(ComStatus.PayConfirmStatus.PUB_FAILED);
				return ans;
			}

			// >> check return ans >>
			if (ComStatus.PayConfirmStatus.SUCCESS!=payconans.getStatus()) {
				throw new CapiException(payconans.getStatus().toString());
			}
			
		}
		catch(CapiException e) {
			throw new CapiException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());//exception handling
		}
						
		// settle two accounts		
		
		int buycid = 0;
		int sellcid = 0;
		
		long quant = paycon.getQuant();// quant must be exceed quant
		
		if('B'== paycon.getSide()){ //payconfirm is deal side
		// dealer buy				
		// publish sell
			buycid = paycon.getCid();
			//sellcid = paycon.getClientid();
			sellcid = paycon.getPoid();
		// check paycon.getClientid == paycon.getPoid()
		}
		
		if('S'== paycon.getSide()) {
		// dealer sell
		// publisher buy
			buycid = paycon.getPoid();
			//sellcid = paycon.getClientid();
			sellcid = paycon.getCid();
		// check paycon.getClientid() == paycon.getCid()
		}
		
		if(('B'!= paycon.getSide())&&('S'!= paycon.getSide())) {
			ans.setStatus(ComStatus.PayConfirmStatus.IN_MSG_ERR);
			return ans;
		}
		
			
		AccRow buyacc = null;
		AccRow sellacc = null;
		try {
			buyacc = this.acc.selectAccRow(buycid, paycon.getPnsgid(), paycon.getPnsid());
			sellacc = this.acc.selectAccRow(sellcid, paycon.getPnsgid(), paycon.getPnsid());
			if((buyacc==null)||(sellacc==null)){
				ans.setStatus(ComStatus.PayConfirmStatus.DB_ACC_MISS);
				return ans;
			}
			// check if acc data corrupt
			
		}
		catch(Exception e) {
			ans.setStatus(ComStatus.PayConfirmStatus.DB_ACC_MISS);
			return ans;
		}
					
		int retcode = 0;
		try {				
			retcode = this.acc.updateBSAccRow(buycid, paycon.getPnsgid(), paycon.getPnsid(), 
					buyacc.getBalance()+quant, buyacc.getFreemargin()+quant, buyacc.getPnl()+quant);
			if(retcode == 0) {
				ans.setStatus(ComStatus.PayConfirmStatus.DB_ACC_MISS);;
				return ans;
			}
							
			retcode = this.acc.updateSSAccRow(sellcid, paycon.getPnsgid(), paycon.getPnsid(), 
					sellacc.getBalance()-quant, sellacc.getMargin()-quant, sellacc.getPnl()-quant);
			if(retcode == 0) {
				throw new CapiException(ComStatus.PayConfirmStatus.DB_ACC_MISS.toString());
			}
		}
		catch(CapiException e) {
			throw new CapiException(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception(ComStatus.PayConfirmStatus.DB_ACC_MISS.toString());
		}
		
		// A account publish sell side from margin sub quant -> pnl sub quant
				
		// B account deal buy side from 
				
		// how to deal with payconfirm				
		
		return payconans;
	} 

	public CDCancelAns dcancel(CDCancel can, CDCancelAns ans) throws CapiException, Exception{
		
		// now set two accounts
		int buycid = 0;
		int sellcid = 0;
		AccRow buyacc = null;
		AccRow sellacc = null;
		
		// for buy-dealer no margin locking, for sell-publisher already done 
		if('B'==can.getSide()) {
			buycid = can.getCid();
			sellcid = can.getPoid();	
		}
		
		// for seller dealer lock is already done, for sell-publisher already done
		// need to unlock dealer's margin
		if('S'==can.getSide()) {
			buycid = can.getPoid();
			sellcid = can.getCid();			
		}
		
		if(('B'!=can.getSide())&&('S'!=can.getSide())){
			ans.setStatus(ComStatus.DCancelStatus.IN_MSG_ERR);
			return ans;
		}
		
		// get buyacc and sellacc
		// select sellcid deal's acc
	
		try {
			buyacc = this.acc.selectAccRow(buycid, can.getPnsgid(), can.getPnsid());
			sellacc = this.acc.selectAccRow(sellcid, can.getPnsgid(), can.getPnsid());
			if((buyacc==null)||(sellacc==null)){
				ans.setStatus(ComStatus.DCancelStatus.DB_ACC_MISS);
				return ans;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new CapiException(ComStatus.DCancelStatus.DB_ACC_MISS.toString());
		}
		
		
		// send things to pub dcancel
		CDCancelAns dcanans = null;
		try {
			dcanans = this.rest.postForObject(this.url+"/dcancel",can, CDCancelAns.class);
			if (dcanans == null) {
				ans.setStatus(ComStatus.DCancelStatus.PUB_FAILED);
				return ans;
			}

			// >> check return ans >>
			if (ComStatus.DCancelStatus.SUCCESS!=dcanans.getStatus()) {
				throw new CapiException(dcanans.getStatus().toString());
			}
			
		}
		catch(CapiException e) {
			throw new CapiException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CapiException(ComStatus.DCancelStatus.UNKNOWN.toString());//exception handling
		}
		
		// everything success 
		// update both accounts
		if('B'==can.getSide()) {
			ans.setStatus(dcanans.getStatus());
			return ans;
		}
		
		if('S'==can.getSide()) {
			// update  seller-dealer locked margin
			int retcode = 0;
			long margin = sellacc.getMargin();
			long freemargin = sellacc.getFreemargin();
			long quant = can.getQuant();
			// check margin
			if(margin<quant) {
				ans.setStatus(ComStatus.DCancelStatus.DB_ACC_ERR);
				return ans;
			}			
			
			try {
				retcode = this.acc.updateSDCanAccRow(sellcid, can.getPnsgid(), can.getPnsid(), margin-quant, freemargin+quant);
				if(retcode==0) {
					ans.setStatus(ComStatus.DCancelStatus.DB_ACC_MISS);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw new CapiException(ComStatus.DCancelStatus.DB_ACC_ERR.toString());
			}
			
			ans.setStatus(ComStatus.DCancelStatus.SUCCESS);
			return ans;
		}
		
		ans.setStatus(ComStatus.DCancelStatus.UNKNOWN);
		return ans;
	}
	
	public CPCancelAns pcancel(CPCancel can, CPCancelAns ans) throws CapiException, Exception{
		
		// input check //those already checked
		if(can.getClientid()!=can.getPoid()) {
			ans.setStatus(ComStatus.PCancelStatus.IN_MSG_ERR);
			return ans;
		}
		
		if(('B'!=can.getSide())&&('S'!=can.getSide())){
			ans.setStatus(ComStatus.PCancelStatus.IN_MSG_ERR);
			return ans;
		}
		
		// now set two accounts
		int pubcid = can.getPoid();
		AccRow pubacc = null;
		
		pubacc = this.acc.selectAccRow(pubcid, can.getPnsgid(), can.getPnsid());
		if(pubacc==null) {
			ans.setStatus(ComStatus.PCancelStatus.DB_ACC_MISS);
			return ans;
		}
		
		// for buy-dealer no margin locking, for sell-publisher need to free locked margin 		
		// for seller dealer lock is already done, for buy-publisher already done
		// need to unlock dealer's margin
		
		CPCancelAns pcanans = null;
		try {
			pcanans = this.rest.postForObject(this.url+"/pcancel",can, CPCancelAns.class);
			if (pcanans == null) {
				ans.setStatus(ComStatus.PCancelStatus.PUB_FAILED);
				return ans;
			}

			// >> check return ans >>
			if (ComStatus.PCancelStatus.SUCCESS!=pcanans.getStatus()) {
				throw new CapiException(pcanans.getStatus().toString());
			}
			
		}
		catch(CapiException e) {
			throw new CapiException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CapiException(ComStatus.PCancelStatus.UNKNOWN.toString());//exception handling
		}
		
		// for buy-publisher nothing need to be done, already done in pub 		
		if('B'==can.getSide()) {
			ans.setStatus(pcanans.getStatus());
			ans.setAmount(pcanans.getAmount());
			return ans;
		}
		
		// for sell-publisher margin need to be freed from locking
		if('S'==can.getSide()) {
			long margin = pubacc.getMargin();
			long freemargin = pubacc.getFreemargin();
			long amount = pcanans.getAmount(); // amount must be greate than 0
			
			// check margin
			if(amount<0) {
				ans.setStatus(ComStatus.PCancelStatus.UNKNOWN);
				return ans;
			}
			
			if(margin<amount) {
				ans.setStatus(ComStatus.PCancelStatus.UNKNOWN);
				return ans;
			}
			
			int retcode = 0;
			try {
				retcode = this.acc.updateSPCanAccRow(pubcid, can.getPnsgid(), can.getPnsid(), margin-amount, freemargin+amount);
				if(retcode == 0) {					
					ans.setStatus(ComStatus.PCancelStatus.DB_ACC_ERR);
					return ans;
				}					
			}
			catch(Exception e) {
				e.printStackTrace();
				throw new CapiException(ComStatus.PCancelStatus.DB_ACC_ERR.toString());
			}
			
			ans.setAmount(amount);
			ans.setStatus(pcanans.getStatus());  
			return ans;
		}
		
		ans.setStatus(ComStatus.PCancelStatus.UNKNOWN);		
		return ans;
	}
	
	// deposit and withdraw
	public CDepositAccAns depositAcc(CDepositAcc dp, CDepositAccAns ans) throws CapiException, Exception{

		// first check inout order status
		OrdRow ordrow = null;
		try {
			ordrow = this.ord.selectOrdRow(dp.getOid().toString(), dp.getClientid(), dp.getPnsgid(), dp.getPnsid(), "D");
		}
		catch(Exception e){
			e.printStackTrace();
			ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
			return ans;
		}
		
		// SUCCESS or FAILED status 
		if(ComStatus.DepositOrdStatus.PROCEEDING!=dp.getConlvl()) {
			if(ordrow == null) {
				ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
				return ans;
			}
			
			if((ComStatus.DepositOrdStatus.SUCCESS!=dp.getConlvl())&&(ComStatus.DepositOrdStatus.FAILED!=dp.getConlvl())) {
				ans.setStatus(ComStatus.DepositAccStatus.WRONG_ORD_STATUS);
				return ans;
			}
			
			// check ordstatus and quant with database
			try {
				// check status
				if(ComStatus.DepositOrdStatus.PROCEEDING!=ComStatus.DepositOrdStatus.valueOf(ordrow.getStatus())) {
					ans.setStatus(ComStatus.DepositAccStatus.WRONG_ORD_STATUS);
					return ans;
				}
				
				// check quant
				if(ordrow.getQuant()!= dp.getQuant()) {
					ans.setStatus(ComStatus.DepositAccStatus.WRONG_ORD_QUANT);
					return ans;
				}
								
			}catch(Exception e) {
				ans.setStatus(ComStatus.DepositAccStatus.WRONG_ORD_STATUS);
				return ans;
			}			
			
			// #### update ordstatus ###
			
			// proceeding -> success			
			if(ComStatus.DepositOrdStatus.SUCCESS==dp.getConlvl()) {				
				ordrow.setStatus(ComStatus.DepositOrdStatus.SUCCESS.toString());
				int cv = 0;
				try {
					cv = this.ord.updateDepositOrdRow(ordrow);
					if(cv==0) {
						ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
						return ans;
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
					return ans;
				}
			}
			
			// proceeding -> failed
			if(ComStatus.DepositOrdStatus.FAILED==dp.getConlvl()) {
				ordrow.setStatus(ComStatus.DepositOrdStatus.FAILED.toString());
				int cv = 0;
				try{
					cv = this.ord.updateDepositOrdRow(ordrow);
					if(cv==0) {
						ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
						return ans;
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
					return ans;
				}
			}
			
		}
		else // PROCEEDING STATUS
		{
			if(ordrow!=null) {
				ans.setStatus(ComStatus.DepositAccStatus.WRONG_ORD_STATUS);
				return ans;
			}
			
			ordrow = new OrdRow();
			ordrow.setTimestamp(System.currentTimeMillis());
			ordrow.setOid(dp.getOid().toString());
			ordrow.setCid(dp.getClientid());
			ordrow.setSide('D');
			ordrow.setPnsgid(dp.getPnsgid());
			ordrow.setPnsid(dp.getPnsid());
			ordrow.setQuant(dp.getQuant());
			ordrow.setTranid(dp.getTranid());
			ordrow.setStatus(dp.getConlvl().toString());
			
			int cv = 0;
			// insert order into inout row
			try {
				cv = this.ord.insertOrdRow(ordrow);
				if(cv==0) {
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ORD_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				return ans;
			}
		}
		
		// #### update ACC row ####
		// select ACC row 
		AccRow accrow = null;
		
		// lock APM
		try {
			accrow = this.acc.selectAccRow(dp.getClientid(), dp.getPnsgid(), dp.getPnsid());
			if (accrow == null) {
				ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB);
				return ans;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB_EX);
			return ans;
		}
				
		long margin = accrow.getMargin();
		long freemargin = accrow.getFreemargin(); 
		long balan = accrow.getBalance();
		long change = accrow.getChangebalan();
		long quant = dp.getQuant();
				
		// status deposit initialize // update ACC
		if(ComStatus.DepositOrdStatus.PROCEEDING == dp.getConlvl()) {
			int cv = 0;
			accrow.setBalance(balan+quant);
			accrow.setMargin(margin+quant);
			accrow.setChangebalan(change+quant);
			
			try {
				//## update acc row
				cv = this.acc.updateDepositAccRow(accrow);
				if(cv==0) {
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
			
			ans.setStatus(ComStatus.DepositAccStatus.SUCCESS);
			return ans;
		}
		
		// status success 
		if(ComStatus.DepositOrdStatus.SUCCESS == dp.getConlvl()) {
			int cv=0;
			accrow.setMargin(margin-quant); // more check need to be added
			accrow.setFreemargin(freemargin+quant);
			
			try {
				// only success using confirm
				cv = this.acc.updateDepositAccRowConfirm(accrow);
				if(cv==0) {
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
			
			ans.setStatus(ComStatus.DepositAccStatus.SUCCESS);
			return ans;
		}
		
		// status failed
		if(ComStatus.DepositOrdStatus.FAILED == dp.getConlvl()) {
			
			int cv = 0;
			accrow.setBalance(balan-quant); 	// more checks need to be added
			accrow.setMargin(margin-quant);		// add lock bit to check
			accrow.setChangebalan(change-quant);// more checks need to be added
			
			try {
				//## update acc row
				cv = this.acc.updateDepositAccRow(accrow);
				if(cv==0) {
					ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.DepositAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
			
			ans.setStatus(ComStatus.DepositAccStatus.SUCCESS);
			return ans;
		}		
		
		ans.setStatus(ComStatus.DepositAccStatus.UNKNOWN);
		return ans;
	}
	
	public CWithdrawAccAns withdrawAcc(CWithdrawAcc wd ,CWithdrawAccAns ans) throws CapiException, Exception{
		
		// first check inout order status		
		OrdRow ordrow = null;
		try {
			ordrow = this.ord.selectOrdRow(wd.getOid().toString(), wd.getClientid(), wd.getPnsgid(), wd.getPnsid(), "W");
		}
		catch(Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ORD_DB);
			return ans;
		}
		//# ORD UPDATE #
		// SUCCESS or FAILED
		if(ComStatus.WithdrawOrdStatus.PROCEEDING!=wd.getConlvl()) {
			if(ordrow==null) {
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ORD_DB);				
				return ans;
			}
			
			if((ComStatus.WithdrawOrdStatus.SUCCESS!=wd.getConlvl())&&(ComStatus.WithdrawOrdStatus.FAILED!=wd.getConlvl())) {
				ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_STATUS);
				return ans;
			}
			
			// check ord db ordstatus and quant with database
			try {
				// check status
				if(ComStatus.WithdrawOrdStatus.PROCEEDING!=ComStatus.WithdrawOrdStatus.valueOf(ordrow.getStatus())) {
					ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_STATUS);
					return ans;
				}
				
				// check quant
				if(ordrow.getQuant()!=wd.getQuant()) {
					ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_QUANT);
					return ans;
				}
								
			}catch(Exception e) {
				ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_STATUS);
				return ans;
			}
			
			// update order status
			
			// PROCEEDING -> SUCCESS
			if(ComStatus.WithdrawOrdStatus.SUCCESS==wd.getConlvl()) {				
				ordrow.setStatus(ComStatus.WithdrawOrdStatus.SUCCESS.toString());
				this.ord.updateWithdrawOrdRow(ordrow);
			}
			
			// PROCEEDING -> FAILED
			if(ComStatus.WithdrawOrdStatus.FAILED==wd.getConlvl()) {
				ordrow.setStatus(ComStatus.WithdrawOrdStatus.FAILED.toString());
				this.ord.updateWithdrawOrdRow(ordrow);
			}
			
		}
		else // PROCEEDING // need to check if ACC has enough 
		{
			if(ordrow!=null) {
				ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_STATUS);
				return ans;
			}
			
			ordrow = new OrdRow();
			ordrow.setTimestamp(System.currentTimeMillis());
			ordrow.setCid(wd.getClientid());
			ordrow.setSide('W');
			ordrow.setPnsgid(wd.getPnsgid());
			ordrow.setPnsid(wd.getPnsid());
			ordrow.setQuant(wd.getQuant());
			ordrow.setStatus(wd.getConlvl().toString());
			
			int cv = 0;
			// insert order into inout row
			try {
				// this need to be done after or changed to be after
				cv = this.ord.insertOrdRow(ordrow);
				if(cv==0) {
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ORD_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				return ans;
			}
		}
					
		//# ACC UPDATE # // select ACC row //
		// #### update ACC row ####
		// select ACC row 
		AccRow accrow = null;
				
		// lock APM
		try {
			accrow = this.acc.selectAccRow(wd.getClientid(), wd.getPnsgid(), wd.getPnsid());
			if (accrow == null) {
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB);
				return ans;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
			return ans;
		}
						
		long margin = accrow.getMargin();
		long freemargin = accrow.getFreemargin(); 
		long balan = accrow.getBalance();
		long change = accrow.getChangebalan();
		long quant = wd.getQuant();
		
		// CHECK IF WITHDRAW VALID
		if((quant<=0)||(freemargin<0)) {
			int cv=0;
			ordrow.setStatus(ComStatus.WithdrawOrdStatus.UNKNOWN.toString());
			try {
				cv = this.ord.insertOrdRow(ordrow);
				if(cv==0) {
					// should be exception instead
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ORD_DB);
					return ans;
				}
			}catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
				return ans;
			}			
			ans.setStatus(ComStatus.WithdrawAccStatus.UNKNOWN);
			return ans;
		}
		
		if(quant>freemargin) {
			int cv = 0;
			ordrow.setStatus(ComStatus.WithdrawOrdStatus.REJECT.toString());
			try {
				cv = this.ord.insertOrdRow(ordrow);
				if(cv==0) {
					// should be exception instead
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ORD_DB);
					return ans;
				}
			}catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
			ans.setStatus(ComStatus.WithdrawAccStatus.WRONG_ORD_QUANT);
			return ans;
		}
		
		// status withdraw initialize // update ACC
		if(ComStatus.WithdrawOrdStatus.PROCEEDING == wd.getConlvl()) {
			int cv = 0;
			accrow.setMargin(margin+quant);
			accrow.setFreemargin(freemargin-quant);
			
			try {
				//## update acc row ##
				cv = this.acc.updateWithdrawAccRow(accrow);// margin/freemargin
				if(cv==0) {
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
					
			ans.setStatus(ComStatus.WithdrawAccStatus.SUCCESS);
			return ans;
		}
				
		// status success 
		if(ComStatus.WithdrawOrdStatus.SUCCESS == wd.getConlvl()) {
			// check validation
			if(margin<quant) {				
				ans.setStatus(ComStatus.WithdrawAccStatus.UNKNOWN);
				return ans;
			}
			
			int cv=0;			
			
			accrow.setBalance(balan-quant);
			accrow.setMargin(margin-quant); // more check need to be added
			accrow.setChangebalan(change-quant);
					
			try {
				// only success using confirm
				cv = this.acc.updateWithdrawAccRowConfirm(accrow);
				if(cv==0) {
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
					
			ans.setStatus(ComStatus.WithdrawAccStatus.SUCCESS);
			return ans;
		}
				
		// status failed
		if(ComStatus.WithdrawOrdStatus.FAILED == wd.getConlvl()) {
					
			// check validation
			if(margin<quant) {
				ans.setStatus(ComStatus.WithdrawAccStatus.UNKNOWN);
				return ans;
			}
			
			int cv = 0;
			
			accrow.setMargin(margin-quant);			// add lock bit to check
			accrow.setFreemargin(freemargin+quant);
						
			try {
				//## update acc row
				cv = this.acc.updateWithdrawAccRow(accrow);
				if(cv==0) {
					ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB);
					return ans;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				ans.setStatus(ComStatus.WithdrawAccStatus.MISS_ACC_DB_EX);
				return ans;
			}
					
			ans.setStatus(ComStatus.WithdrawAccStatus.SUCCESS);
			return ans;
		}		
				
		ans.setStatus(ComStatus.WithdrawAccStatus.UNKNOWN);	
		return ans;
	}
}
