package br.com.cassiopaixao.account.payment.api.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cassiopaixao.account.payment.api.event.RecursoCriadoEvent;
import br.com.cassiopaixao.account.payment.api.exceptionhandler.AccountPaymentExceptionHandler.Error;
import br.com.cassiopaixao.account.payment.api.model.Lancamento;
import br.com.cassiopaixao.account.payment.api.repository.LancamentoRepository;
import br.com.cassiopaixao.account.payment.api.repository.filter.LancamentoFilter;
import br.com.cassiopaixao.account.payment.api.service.LancamentoService;
import br.com.cassiopaixao.account.payment.api.service.exception.PessoaInexistenteOuInativoException;

@RestController()
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping
	public List<Lancamento> pesquisar(LancamentoFilter lancamentoFilter){
		return lancamentoRepository.filtrar(lancamentoFilter);
	}

	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> getLancamentoPorId(@PathVariable Long codigo) {
		Lancamento lancamento = lancamentoRepository.findOne(codigo);
		return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build();
	}
	
	@PostMapping
	public ResponseEntity<Lancamento> salvarLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	@ExceptionHandler({PessoaInexistenteOuInativoException.class})
	public ResponseEntity<Object> handlePessoaInexistenteOuInativoException(PessoaInexistenteOuInativoException ex){
		String messageUser = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String messageDeveloper = ex.toString();
		List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
		return ResponseEntity.badRequest().body(errors);
	}
	

}
