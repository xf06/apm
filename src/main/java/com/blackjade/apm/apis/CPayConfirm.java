package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.PayConfirmStatus;

//0x7007	{requestid, clientid, oid, side, pnsid, pnsgid, price, quant}

public class CPayConfirm {
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
	private int quant;

	public CPayConfirm() {
		this.messageid = "7007";
	}

	public PayConfirmStatus reviewData() {
		
		if(!this.messageid.equals("7007"))
			return ComStatus.PayConfirmStatus.WRONG_MSGID;

		if (this.requestid == null)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if (this.oid == null)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if (this.cid <= 0)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;
		
		if (('S' != this.side) && ('B' != this.side))
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if (this.pnsoid == null)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if (this.poid < 0)
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;
		
		//if ((this.pnsid <= 0)||(this.pnsgid <= 0))
		//	return ComStatus.PayConfirmStatus.IN_MSG_ERR;

		if ((this.price <= 0)||(this.quant <= 0))
			return ComStatus.PayConfirmStatus.IN_MSG_ERR;
		
		// check logic 
		if('B'==this.getSide()) {
			if(this.getClientid()!=this.getPoid())
				return ComStatus.PayConfirmStatus.IN_MSG_ERR;
		}
		
		if('S'==this.getSide()) {
			if(this.getClientid()!=this.getCid())
				return ComStatus.PayConfirmStatus.IN_MSG_ERR;
		}
		
		return ComStatus.PayConfirmStatus.SUCCESS;
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

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

}
