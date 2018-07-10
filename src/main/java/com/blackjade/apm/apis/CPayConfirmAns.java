package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.PayConfirmStatus;

//0x7008	{requestid, clientid, oid, side, pnsid, pnsgid, price, quant, status}

public class CPayConfirmAns {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int cid;
	private char side;
	private UUID pnsoid; // pns order id
	private int poid; // product owner id
	private int pnsid;
	private int pnsgid;
	private long price;
	private long quant;
	private PayConfirmStatus status;

	public CPayConfirmAns() {
	}

	public CPayConfirmAns(UUID requestid) {
		this.messageid = "7008";
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

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
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

	public long getQuant() {
		return quant;
	}

	public void setQuant(long quant) {
		this.quant = quant;
	}

	public PayConfirmStatus getStatus() {
		return status;
	}

	public void setStatus(PayConfirmStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CPayConfirmAns [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid
				+ ", oid=" + oid + ", cid=" + cid + ", side=" + side + ", pnsoid=" + pnsoid + ", poid=" + poid
				+ ", pnsid=" + pnsid + ", pnsgid=" + pnsgid + ", price=" + price + ", quant=" + quant + ", status="
				+ status + "]";
	}

}
