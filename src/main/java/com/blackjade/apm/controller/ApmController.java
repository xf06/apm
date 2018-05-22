package com.blackjade.apm.controller;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.blackjade.apm.apis.CDeal;
import com.blackjade.apm.apis.CDealAns;
import com.blackjade.apm.apis.CPublish;
import com.blackjade.apm.apis.CPublishAns;
import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.apis.ComStatus.PublishStatus;
import com.blackjade.apm.controller.service.ApmService;
import com.blackjade.apm.dao.AccDao;

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
				ans.setStatus(st);
				return ans;
			}
		}
		catch(Exception e) {
			// this part should return the ans about a few things
			// throw exception			
			e.printStackTrace();
			ans.setStatus(ComStatus.PublishStatus.DATABASE_ERR);
			return ans;
		}
				
		// return success
		return ans;
	}	

//	@RequestMapping(value = "/deal", method = RequestMethod.POST)
//	@ResponseBody
//	public CDealAns cQueryPublish(CDeal deal){
//		
//		CDealAns ans;
//		
//		return ans;
//	}
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
