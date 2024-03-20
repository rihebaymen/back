package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invetation;
import com.example.meta.store.werehouse.Entities.Provider;

public interface InvetationRepository extends BaseRepository<Invetation, Long> {

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	@Query("SELECT i FROM Invetation i WHERE"
			+ " i.companySender.id = :companyId"
			+ " OR i.companyReciver.id = :companyId"
			+ " OR i.client.id = :clientId"
			+ " OR i.provider.id = :providerId"
			+ " OR i.user.id = :userId"
			)
	List<Invetation> findAllByClientIdOrProviderIdOrCompanyIdOrUserId(Long clientId, Long providerId, Long companyId, Long userId);

	void deleteByClientIdAndCompanySenderId(Long id, Long id2);

	void deleteByProviderIdAndCompanySenderId(Long id, Long id2);
	
	@Query("SELECT i FROM Invetation i WHERE i.user.id = :id")
	Invetation findByWorkerId(Long id);



}


