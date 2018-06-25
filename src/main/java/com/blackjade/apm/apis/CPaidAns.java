package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.PaidStatus;

// 0x7006	{requestid, clientid, oid, side, pnsid, pnsgid, price, quant, status}

public class CPaidAns {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int cid;
	private char side;
	private UUID pnsoid;
	private int pnsid;
	private int pnsgid;
	private long price;
	private int quant;
	private PaidStatus status;

	public CPaidAns() {
		this.messageid = "7006";
	}

	public CPaidAns(UUID requestid) {
		this.requestid = requestid;
		this.messageid = "7006";
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

	public PaidStatus getStatus() {
		return status;
	}

	public void setStatus(PaidStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CPaidAns [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid + ", oid="
				+ oid + ", cid=" + cid + ", side=" + side + ", pnsoid=" + pnsoid + ", pnsid=" + pnsid + ", pnsgid="
				+ pnsgid + ", price=" + price + ", quant=" + quant + ", status=" + status + "]";
	}

}
