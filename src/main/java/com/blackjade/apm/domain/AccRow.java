package com.blackjade.apm.domain;

//
public class AccRow {

	private int cid;
	private int cgid;
	private int pnsgid;
	private int pnsid;

	private long balance;
	private long margin;
	private long freemargin;
	private long prebalan;
	private long changebalan;
	private long pnl;
	private String cnetadd; // coin net addresss

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getCgid() {
		return cgid;
	}

	public void setCgid(int cgid) {
		this.cgid = cgid;
	}

	public int getPnsgid() {
		return pnsgid;
	}

	public void setPnsgid(int pnsgid) {
		this.pnsgid = pnsgid;
	}

	public int getPnsid() {
		return pnsid;
	}

	public void setPnsid(int pnsid) {
		this.pnsid = pnsid;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public long getMargin() {
		return margin;
	}

	public void setMargin(long margin) {
		this.margin = margin;
	}

	public long getFreemargin() {
		return freemargin;
	}

	public void setFreemargin(long freemargin) {
		this.freemargin = freemargin;
	}

	public long getPrebalan() {
		return prebalan;
	}

	public void setPrebalan(long prebalan) {
		this.prebalan = prebalan;
	}

	public long getChangebalan() {
		return changebalan;
	}

	public void setChangebalan(long changebalan) {
		this.changebalan = changebalan;
	}

	public long getPnl() {
		return pnl;
	}

	public void setPnl(long pnl) {
		this.pnl = pnl;
	}

	public String getCnetadd() {
		return cnetadd;
	}

	public void setCnetadd(String cnetadd) {
		this.cnetadd = cnetadd;
	}

}
