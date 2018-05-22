package com.blackjade.apm.apis;

import java.util.UUID;
//7002 {requestid, clientid, oid, side, pnsid, pnsgid, price, quant, min, max, status}

import com.blackjade.apm.apis.ComStatus.PublishStatus;

public class CPublishAns {
	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private char side;
	private int pnsid;
	private int pnsgid;
	private long price;
	private int quant;
	private long min;
	private long max;
	private PublishStatus status;
	
	public CPublishAns() {}
	
	public CPublishAns(UUID requestid) {
		this.messageid = "7002";
		this.requestid = requestid;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public UUID getRequestid() {
		return requestid;
	}

	public void setRequestid(UUID requestid) {
		this.requestid = requestid;
	}

	public int getClientid() {
		return clientid;
	}

	public void setClientid(int clientid) {
		this.clientid = clientid;
	}

	public UUID getOid() {
		return oid;
	}

	public void setOid(UUID oid) {
		this.oid = oid;
	}

	public char getSide() {
		return side;
	}

	public void setSide(char side) {
		this.side = side;
	}

	public int getPnsid() {
		return pnsid;
	}

	public void setPnsid(int pnsid) {
		this.pnsid = pnsid;
	}

	public int getPnsgid() {
		return pnsgid;
	}

	public void setPnsgid(int pnsgid) {
		this.pnsgid = pnsgid;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}


	public PublishStatus getStatus() {
		return status;
	}

	public void setStatus(PublishStatus status) {
		this.status = status;
	}
	
}
