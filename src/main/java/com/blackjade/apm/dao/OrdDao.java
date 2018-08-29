package com.blackjade.apm.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.blackjade.apm.domain.OrdRow;

@Component
public interface OrdDao {
	
	// select Ordrow
	public OrdRow selectOrdRow(@Param(value="oid") String oid, @Param(value = "cid") int cid, 
			@Param(value = "pnsgid") int pnsgid, @Param(value = "pnsid") int pnsid, @Param(value="side") String side);
	
	// insert order
	public int insertOrdRow(OrdRow ordrow);
	
	// update for deposit
	public int updateDepositOrdRow(OrdRow order);
	
	// update for withdraw
	public int updateWithdrawOrdRow(OrdRow order);
	
}
