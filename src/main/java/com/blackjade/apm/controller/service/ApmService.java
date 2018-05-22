package com.blackjade.apm.controller.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.blackjade.apm.apis.CPublish;
import com.blackjade.apm.apis.CPublishAns;
import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.apis.ComStatus.PublishStatus;
import com.blackjade.apm.dao.AccDao;
import com.blackjade.apm.domain.AccRow;

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
		this.url = "http://localhost:" + port + "/publish";
		this.rest = new RestTemplate();
	}

	public CPublishAns publishApm(CPublish pub, CPublishAns ans) throws Exception {

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
			throw new Exception(ComStatus.PublishStatus.ACC_DB_ERR.toString());
		}

		// update PnS

		CPublishAns pubans = null;
		try {
			pubans = this.rest.postForObject(this.url, pub, CPublishAns.class);
			if (pubans == null) {
				throw new Exception(ComStatus.PublishStatus.PUB_FAILED.toString());
			}

			// >> check return ans >>
			if (ans.getStatus() != ComStatus.PublishStatus.SUCCESS) {
				throw new Exception(ans.getStatus().toString());
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		// success or errors will all be returned
		return pubans;
	}

}
