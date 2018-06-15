package com.blackjade.apm.apis;

//cDCancelAns	0x7010	{requestid, clientid, oid, cid, side, pnsoid, poid, pnsid, pnsgid, price, quant, status}	HTTP
import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.DCancelStatus;

public class CDCancelAns {

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
	private DCancelStatus status;

	public CDCancelAns(UUID requestid) {
		this.messageid = "7010";
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

	public DCancelStatus getStatus() {
		return status;
	}

	public void setStatus(DCancelStatus status) {
		this.status = status;
	}

}
