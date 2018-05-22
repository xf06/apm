package com.blackjade.apm.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.blackjade.apm.domain.AccRow;

@Component
public interface AccDao {

	// select Accrow
	public AccRow selectAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid);

	// update for publish
	public int updatePubAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value = "margin") long margin,
			@Param(value = "freemargin") long freemargin);
	
	//
	
	
}
