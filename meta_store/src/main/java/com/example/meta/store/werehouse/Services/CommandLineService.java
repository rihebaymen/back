package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.ClientInvoice;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Mappers.CommandLineMapper;
import com.example.meta.store.werehouse.Mappers.InvoiceMapper;
import com.example.meta.store.werehouse.Repositories.CommandLineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandLineService extends BaseService<CommandLine, Long> {

	
	private final CommandLineMapper commandLineMapper;

	private final InvoiceService invoiceService;
	
	private final ArticleService articleService;
	
	private final InventoryService inventoryService;
	
	private final CommandLineRepository commandLineRepository;
	
	private final ClientInvoiceService clientInvoiceService;
	

    DecimalFormat df = new DecimalFormat("#.###");

	public ResponseEntity<InputStreamResource> insertLine(List<CommandLineDto> commandLinesDto, Company company, 
			Long clientId, String type) {
		List<CommandLine> commandLines = new ArrayList<>();
		List<Article> articles = new ArrayList<>();
		List<CompanyArticle> companyArticles = new ArrayList<>();
		Invoice invoice = invoiceService.addInvoice(company,clientId);
		
		for(CommandLineDto i : commandLinesDto) {
			Article article = articleService.findByCompanyArticleId(i.getCompanyArticle());
			CompanyArticle companyArticle = articleService.findCompanyArticleById(i.getCompanyArticle());
			companyArticles.add(companyArticle);
			articles.add(article);
			if(article.getQuantity()-i.getQuantity()<0) {
				throw new RecordNotFoundException("There Is No More "+article.getLibelle());
			}
			article.setQuantity(article.getQuantity()-i.getQuantity());
			articleService.insert(article);
		CommandLine commandLine = commandLineMapper.mapToEntity(i);
		commandLine.setInvoice(invoice);
		String prix_article_tot = df.format(i.getQuantity()*article.getCost()*article.getMargin());
		prix_article_tot = prix_article_tot.replace(",", ".");
		commandLine.setPrixArticleTot(Double.parseDouble(prix_article_tot));
		String tot_tva = df.format(article.getTva()*i.getQuantity()*article.getCost()*article.getMargin()/100);
		tot_tva = tot_tva.replace(",", ".");
		commandLine.setTotTva(Double.parseDouble(tot_tva));
		commandLines.add(commandLine);
		}
		super.insertAll(commandLines);
		List<CommandLine> commandLine = commandLineRepository.findByInvoiceCode(invoice.getCode());
		double totHt= 0;
		double totTva= 0;
		double totTtc= 0;
		for(CommandLine i : commandLine) {
			 totHt =+ i.getPrixArticleTot();
			 totTva =+ i.getTotTva();
			 totTtc =+ totHt+totTva;
		}
		invoice.setPrix_article_tot(totHt);
		invoice.setTot_tva_invoice(totTva);
		invoice.setPrix_invoice_tot(totTtc);
		System.out.println("befor insert invoice in command line service");
		invoiceService.insert(invoice);
		System.out.println("after insert invoice in command line service");
	//	articleService.impactInvoice(commandLinesDto,id,articles, companyArticles); //bay the articles for client
		clientInvoiceService.addClientInvoiceService( clientId, company.getId(), invoice);
		System.out.println("befor impact invoice in command line service");
		inventoryService.impacteInvoice(company,commandLinesDto,articles,companyArticles,clientId);
		System.out.println("after impact invoice in command line service");
	
		if (type.equals("pdf-save-client") ) {	
		
			return invoiceService.export(company,commandLines,companyArticles);
			
		}
		
		return null;
	}

	public List<CommandLineDto> getCommandLines(Long invoiceId) {
		List<CommandLine> commandLines = commandLineRepository.findAllByInvoiceId(invoiceId);
		List<CommandLineDto> commandLinesDto = new ArrayList<>();
		for(CommandLine i : commandLines) {
			CommandLineDto commandLineDto = commandLineMapper.mapToDto(i);
			commandLinesDto.add(commandLineDto);
		}
		return commandLinesDto;
	}





}
