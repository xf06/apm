package com.blackjade.apm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.blackjade.apm.apis.CDCancel;
import com.blackjade.apm.apis.CDCancelAns;
import com.blackjade.apm.apis.CDeal;
import com.blackjade.apm.apis.CDealAns;
import com.blackjade.apm.apis.CPCancel;
import com.blackjade.apm.apis.CPCancelAns;
import com.blackjade.apm.apis.CPayConfirm;
import com.blackjade.apm.apis.CPayConfirmAns;
import com.blackjade.apm.apis.CPublish;
import com.blackjade.apm.apis.CPublishAns;
import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.apis.ComStatus.DCancelStatus;
import com.blackjade.apm.apis.ComStatus.DealStatus;
import com.blackjade.apm.apis.ComStatus.PCancelStatus;
import com.blackjade.apm.apis.ComStatus.PayConfirmStatus;
import com.blackjade.apm.apis.ComStatus.PublishStatus;
import com.blackjade.apm.controller.service.ApmService;
import com.blackjade.apm.dao.AccDao;
import com.blackjade.apm.exception.CapiException;

@RestController
public class ApmController {

	@Autowired
	private AccDao accdao;
	
	@Autowired 
	private ApmService apms;
	
	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	@ResponseBody
	public CPublishAns cQueryPublish(@RequestBody CPublish pub) {
		
		// check input data
		PublishStatus st = pub.reviewData();
				
		// construct reply ans
		CPublishAns ans = new CPublishAns(pub.getRequestid());
		ans.setClientid(pub.getClientid());
		
		// ans.setOid(); // publisher provide oid
		ans.setSide(pub.getSide());
		ans.setPnsgid(pub.getPnsgid());
		ans.setPnsid(pub.getPnsid());
		
		ans.setPrice(pub.getPrice());
		ans.setQuant(pub.getQuant());
		ans.setMax(pub.getMax());
		ans.setMin(pub.getMin());
			
		//#//private UUID oid;
		//#//private PublishStatus status;
				
		// check review data
		if(st!=ComStatus.PublishStatus.SUCCESS) {
			ans.setStatus(st);
			return ans;
		}

		// update apm and pub
		try {
			
			ans = this.apms.publishApm(pub, ans); // if not ok just throw 			
			if(ComStatus.PublishStatus.SUCCESS!=ans.getStatus()) {
				return ans;
			}
		}
		catch(CapiException e) {
			ans.setStatus(ComStatus.PublishStatus.valueOf(e.getMessage()));
			return ans;
		}
		catch(Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.PublishStatus.DATABASE_ERR);
			return ans;
		}
				
		// return success
		return ans;
	}	

	@RequestMapping(value = "/deal", method = RequestMethod.POST)
	@ResponseBody
	public CDealAns cQueryDeal(@RequestBody CDeal deal){
		
		// check input 
		DealStatus st = deal.reviewData(); 
			
		// construct reply ans 
		CDealAns ans = new CDealAns(deal.getRequestid());
		
		ans.setClientid(deal.getClientid());		
		ans.setPnsoid(deal.getPnsoid());		
		ans.setPoid(deal.getPoid());
		
		ans.setPnsid(deal.getPnsid());
		ans.setPnsgid(deal.getPnsgid());
		
		ans.setPrice(deal.getPrice());
		ans.setQuant(deal.getQuant());
		ans.setSide(deal.getSide());
		// status and oid
		
		if(ComStatus.DealStatus.SUCCESS!=st) {
			ans.setStatus(st);
			return ans;
		}
		
		// update apm and pub
		try {
			ans = this.apms.dealApm(deal, ans);
			if(ComStatus.DealStatus.SUCCESS!=ans.getStatus()) {
				return ans;
			}
		}
		catch(CapiException e) {
			ans.setStatus(ComStatus.DealStatus.valueOf(e.getMessage()));
			return ans;
		}		
		catch(Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.DealStatus.DATABASE_ERR);
			return ans;			
		}
		
		// if everything ok return success
		return ans;
	}
		
	@RequestMapping(value = "/payconfirm", method = RequestMethod.POST)
	@ResponseBody
	public CPayConfirmAns cQueryPayConfirm(@RequestBody CPayConfirm cpaycon){
		
		// check status
		PayConfirmStatus st = cpaycon.reviewData();
		
		CPayConfirmAns ans = new CPayConfirmAns(cpaycon.getRequestid());
		
		ans.setClientid(cpaycon.getClientid());
		ans.setOid(cpaycon.getOid());
		ans.setCid(cpaycon.getCid());
		ans.setSide(cpaycon.getSide());
		ans.setPnsoid(cpaycon.getPnsoid());
		ans.setPoid(cpaycon.getPoid());
		
		ans.setPnsgid(cpaycon.getPnsgid());
		ans.setPnsid(cpaycon.getPnsid());
		ans.setPrice(cpaycon.getPrice());
		ans.setQuant(cpaycon.getQuant());
		
		// check status
		if(ComStatus.PayConfirmStatus.SUCCESS!=st) {
			ans.setStatus(st);
			return ans;
		}
	
		// send payconfirm to pub
		try {
			ans = this.apms.payconfirm(cpaycon, ans);
			if(ComStatus.PayConfirmStatus.SUCCESS!=ans.getStatus()) {
				return ans;
			}
		}
		catch(CapiException e) {
			ans.setStatus(ComStatus.PayConfirmStatus.valueOf(e.getMessage()));
			return ans;
		}	
		catch(Exception e) {
			e.printStackTrace();
			ans.setStatus(ComStatus.PayConfirmStatus.PC_DATABASE_ERR);
			return ans;
		}		
		
		// if everything ok return success
		return ans;
	} 
		
	public CDCancelAns cDCancel(@RequestBody CDCancel can) {
		
		// in msg check
		DCancelStatus st = can.reviewData();
		CDCancelAns ans = new CDCancelAns(can.getRequestid());
		
		// construct ans
		ans.setClientid(can.getClientid());
		ans.setOid(can.getOid());
		ans.setCid(can.getCid());
		ans.setSide(can.getSide());
		ans.setPnsoid(can.getPnsoid());
		ans.setPoid(can.getPoid());
		ans.setPnsgid(can.getPnsgid());
		ans.setPnsid(can.getPnsid());
		ans.setPrice(can.getPrice());
		ans.setQuant(can.getQuant());
		
		if(ComStatus.DCancelStatus.SUCCESS!=st) {
			ans.setStatus(st);
			return ans;
		}
		
		try {
			ans = this.apms.dcancel(can, ans);
			if(ComStatus.DCancelStatus.SUCCESS!=ans.getStatus())
				return ans;
		}
		catch(CapiException e) {
			ans.setStatus(ComStatus.DCancelStatus.valueOf(e.getMessage()));
			return ans;
		}
		catch(Exception e) {
			ans.setStatus(ComStatus.DCancelStatus.UNKNOWN);
			return ans;
		}		
		
		return ans;// return success here
	}
	
	public CPCancelAns cPCancel(@RequestBody CPCancel can) {
		
		// in msg check
		PCancelStatus st = can.reviewData(); 
				
		
		CPCancelAns ans = new CPCancelAns(can.getRequestid()); 
		
		return ans;
	} 
	
	
	
	
//
//	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
//	@ResponseBody
//	public cQueryPublish(){
//		return ans;
//	}
//
//	@RequestMapping(value = "/payconfirm", method = RequestMethod.POST)
//	@ResponseBody
//	public cQueryPublish(){
//		return ans;
//	}

}
