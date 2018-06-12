package com.blackjade.apm.controller.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.blackjade.apm.apis.CDeal;
import com.blackjade.apm.apis.CDealAns;
import com.blackjade.apm.apis.CPayConfirm;
import com.blackjade.apm.apis.CPayConfirmAns;
import com.blackjade.apm.apis.CPublish;
import com.blackjade.apm.apis.CPublishAns;
import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.dao.AccDao;
import com.blackjade.apm.domain.AccRow;
import com.blackjade.apm.exception.CapiException;

@Transactional
@Component
public class ApmService {

	@Autowired
	private AccDao acc;

	private RestTemplate rest;

	private String url;

	private String port;

	@PostConstruct
	public void apmInit() throws Exception {
		this.port = "8112";
		this.url = "http://localhost:" + port;
		this.rest = new RestTemplate();
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
	
}
