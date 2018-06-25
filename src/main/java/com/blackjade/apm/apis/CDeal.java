package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.DealStatus;

//0x7003	{requestid, clientid, side, pnsid, pnsgid, price, quant}

public class CDeal {
	private String messageid;
	private UUID requestid;
	private int clientid;
	private char side;
	private UUID pnsoid; // pns order id
	private int poid; // product owner id
	private int pnsid; // remote config
	private int pnsgid; // remote config
	private long price;
	private int quant;

	public CDeal() {
		this.messageid = "7003";
	}

	public DealStatus reviewData() {

		if (!this.messageid.equals("7003"))
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (this.requestid == null)
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (('S' != this.side) && ('B' != this.side))
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (this.pnsoid == null)
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (this.poid <= 0)
			return ComStatus.DealStatus.IN_MSG_ERR;

		if (this.quant <= 0)
			return ComStatus.DealStatus.IN_QUANT_ERR;

		if (this.price <= 0)
			return ComStatus.DealStatus.IN_MSG_ERR;

		return ComStatus.DealStatus.SUCCESS;
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

	public UUID getPnsoid() {
		return pnsoid;
	}

	public void setPnsoid(UUID pnsoid) {
		this.pnsoid = pnsoid;
	}

	public int getPoid() {
		return poid;
	}

	public void setPoid(int poid) {
		this.poid = poid;
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

	@Override
	public String toString() {
		return "CDeal [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid + ", side="
				+ side + ", pnsoid=" + pnsoid + ", poid=" + poid + ", pnsid=" + pnsid + ", pnsgid=" + pnsgid
				+ ", price=" + price + ", quant=" + quant + "]";
	}

}
