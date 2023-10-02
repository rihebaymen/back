package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

public interface ArticleRepository extends BaseRepository<Article, Long>{

	@Query("SELECT a FROM Article a WHERE a.provider.id = :providerId AND a.libelle = :libelle")
	List<Article> findAllByLibelleAndProviderIdContaining(String libelle, Long providerId);

	
	@Query(value = "SELECT a FROM Article a WHERE "
			+ " (a.isVisible = :publi"
			+ " AND ABS(a.provider.company.user.logitude - :longitude) <5000 "
			+ " AND ABS(a.provider.company.user.latitude - :latitude) <5000)"
			+ " ORDER BY random() LIMIT 10 "
			)
    List<Article> findRandomArticles(double longitude, double latitude, PrivacySetting publi );

	@Query(value = "SELECT a FROM Article a WHERE"
			+ " (a.provider.id = :providerId) "
			+ " OR (((a.isVisible = 2)"
			+ " OR (a.isVisible = 1 AND (a.provider.id IN (SELECT p.id FROM Client c JOIN c.providers p WHERE c.id = :clientId))))"
			+ " AND ABS(a.provider.company.user.logitude - :longitude) <5000 "
			+ " AND ABS(a.provider.company.user.latitude - :latitude) <5000) "
			+ "ORDER BY random() LIMIT 10 ")
    List<Article> findRandomArticlesPro(double longitude, double latitude,Long providerId,Long clientId);

	@Query("SELECT a FROM Article a WHERE ((a.isVisible = 2) "
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM Client c JOIN c.providers p WHERE c.id = :clientId AND p.id = :providerId)))"
			+ " AND (a.company.id = :companyId) ")
	List<Article> findAllByCompanyId(Long companyId,Long clientId, Long providerId);


	Optional<Article> findBySharedPointAndProviderId(String sharedPoint, Long id);


	List<Article> findAllMyByCompanyId(Long id);


	@Query("SELECT a FROM Article a WHERE (a.category.id = :categoryId AND a.company.id = :companyId)"
			+ " AND (a.isVisible = 2"
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM Client c JOIN c.providers p WHERE c.id = :myClientId AND p.id = :HisProviderId)))"
			)
	List<Article> findAllByCategoryIdAndCompanyId(Long categoryId, Long companyId, Long HisProviderId , Long myClientId);

	@Query("SELECT a FROM Article a WHERE (a.subCategory.id = :subcategoryId AND a.company.id = :companyId)"
			+ " AND (a.isVisible = 2"
			+ " OR (a.isVisible = 1 AND EXISTS (SELECT 1 FROM Client c JOIN c.providers p WHERE c.id = :myClientId AND p.id = :HisProviderId)))"
			)
	List<Article> findAllBySubCategoryIdAndCompanyId( Long subcategoryId, Long companyId,Long HisProviderId , Long myClientId);


	List<Article> findAllMyByCategoryIdAndCompanyId(Long categoryId, Long id);


	List<Article> findAllMyBySubCategoryIdAndCompanyId(Long subcategoryId, Long id);
}
 