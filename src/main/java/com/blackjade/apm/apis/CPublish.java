package com.blackjade.apm.apis;

// 7001 {requestid, clientid, side, pnsid, pnsgid, price, quant, min, max}

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.PublishStatus;

public class CPublish {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private char side;
	private int pnsid;
	private int pnsgid;
	private long price;
	private long quant;
	private long min;
	private long max;

	public CPublish() {
		this.messageid = "7001";
	}

	public PublishStatus reviewData() {

		if (!this.messageid.equals("7001"))
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if (this.requestid == null)
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if ((this.side != 'B') && (this.side != 'S'))
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if ((this.quant <= 0) || (this.price <= 0))
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if ((this.max <= 0) || (this.min <= 0))
			return ComStatus.PublishStatus.IN_MSG_ERR;

		if (this.max < this.min)
			return ComStatus.PublishStatus.IN_MSG_ERR;

		return ComStatus.PublishStatus.SUCCESS;
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

	public long getQuant() {
		return quant;
	}

	public void setQuant(long quant) {
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

	@Override
	public String toString() {
		return "CPublish [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid + ", side="
				+ side + ", pnsid=" + pnsid + ", pnsgid=" + pnsgid + ", price=" + price + ", quant=" + quant + ", min="
				+ min + ", max=" + max + "]";
	}

}
