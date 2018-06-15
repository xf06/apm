package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.apis.ComStatus.DCancelStatus;

//cDCancel	0x701F	{requestid, clientid, oid, cid, side, pnsoid, poid, pnsid, pnsgid, price, quant}	HTTP
public class CDCancel {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int cid;
	private char side; // <B or S>
	private UUID pnsoid;
	private int poid; // product owner id
	private int pnsid;
	private int pnsgid;
	private long price;
	private long quant;

	public DCancelStatus reviewData() {

		if (!this.messageid.equals("701E"))
			return ComStatus.DCancelStatus.WRONG_MSGID;

		if (this.requestid == null)
			return ComStatus.DCancelStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.DCancelStatus.IN_MSG_ERR;

		if (('S' != this.side) && ('B' != this.side))
			return ComStatus.DCancelStatus.IN_MSG_ERR;

		if (this.pnsoid == null)
			return ComStatus.DCancelStatus.IN_MSG_ERR;

		if (this.poid <= 0)
			return ComStatus.DCancelStatus.IN_MSG_ERR;

		if ((this.price <= 0) || (this.quant <= 0))
			return ComStatus.DCancelStatus.IN_MSG_ERR;
		
		// logic check
		if(this.clientid!=this.cid)
			return ComStatus.DCancelStatus.IN_MSG_ERR;
		
		return ComStatus.DCancelStatus.SUCCESS;
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

}

