package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

public interface ArticleRepository extends BaseRepository<Article, Long>{

	/////////////////////////////////////// real work ////////////////////////////////////////////////////////
	@Query(value = "SELECT a FROM Article a WHERE "
			+ " ((a.isVisible = 2 AND a.company.isVisible = 2)"
			+ " AND (a.provider.company.user.longitude BETWEEN :longitude - 5.0 AND :longitude + 5.0) "
		    + " AND (a.provider.company.user.latitude BETWEEN :latitude - 5.0 AND :latitude + 5.0)) "
			+ " ORDER BY random() LIMIT 10 "
			)
    List<Article> findRandomArticles( Double longitude, Double latitude );

	@Query(value = "SELECT a FROM Article a WHERE"
			+ " (a.provider.id = :providerId) "
			+ " OR (((a.isVisible = 2)"
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM ClientCompany cc WHERE cc.client.id = :clientId AND cc.company.id = a.company.id))))"
			+ " AND (a.provider.company.user.longitude BETWEEN :longitude - 5.0 AND :longitude + 5.0) "
		    + " AND (a.provider.company.user.latitude BETWEEN :latitude - 5.0 AND :latitude + 5.0) "
		   	+ " ORDER BY random() LIMIT 10 ")
		List<Article> findRandomArticlesPro( Long providerId, Long clientId, Double longitude, Double latitude);
	
	@Query("SELECT a FROM Article a WHERE ((a.isVisible = 2) "
			+ " OR (a.isVisible = 1 AND ((EXISTS (SELECT 1 FROM ClientCompany cc WHERE cc.client.id = :clientId AND cc.company.id = :companyId))"
			+ " OR (EXISTS (SELECT 1 FROM ProviderCompany pc WHERE pc.provider.id = :providerId AND pc.company.id = :companyId)))))"
			+ " AND (a.company.id = :companyId) ")
	Page<Article> findAllByCompanyId(Long companyId,Long clientId, Long providerId, Pageable pageable);

	Optional<Article> findByCodeAndProviderId(String code, Long id);

	Page<Article> findAllByCompanyIdOrderByCreatedDateDesc(Long id, Pageable pageable);


	@Query("SELECT a FROM Article a WHERE (a.category.id = :categoryId AND a.company.id = :companyId)"
			+ " AND (a.isVisible = 2"
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM ClientCompany cc WHERE cc.client.id = :myClientId AND cc.company.id = :companyId)))"
			)
	List<Article> findAllByCategoryIdAndCompanyId(Long categoryId, Long companyId , Long myClientId);


	@Query("SELECT a FROM Article a WHERE (a.subCategory.id = :subcategoryId AND a.company.id = :companyId)"
			+ " AND (a.isVisible = 2"
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM ClientCompany cc WHERE cc.client.id = :myClientId AND cc.company.id = :companyId)))"
			)
	List<Article> findAllBySubCategoryIdAndCompanyId( Long subcategoryId, Long companyId , Long myClientId);

	List<Article> findAllMyByCategoryIdAndCompanyId(Long categoryId, Long id);

	List<Article> findAllMyBySubCategoryIdAndCompanyId(Long subcategoryId, Long id);
	
	
	/////////////////////////////////////// future work ////////////////////////////////////////////////////////
	@Query("SELECT a FROM Article a WHERE a.provider.id = :providerId AND a.libelle = :libelle")
	List<Article> findAllByLibelleAndProviderIdContaining(String libelle, Long providerId);

	








}
 