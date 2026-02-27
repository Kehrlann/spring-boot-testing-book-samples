package wf.garnier.spring.boot.test.ch5.ticket.sla;

import org.springframework.stereotype.Component;

@Component
public class SlaReportGenerator {

	private final SlaRecordRepository slaRecordRepository;

	private final SlaPolicyProvider policyProvider;

	public SlaReportGenerator(SlaRecordRepository slaRecordRepository, SlaPolicyProvider policyProvider) {
		this.slaRecordRepository = slaRecordRepository;
		this.policyProvider = policyProvider;
	}

	public SlaComplianceSummary generateReport() {
		var records = slaRecordRepository.findAll();
		long total = records.size();
		long breached = records.stream()
			.filter(record -> record.getResolutionTimeMinutes() > policyProvider
				.forPriority(record.getPriority())
				.maxResolutionMinutes())
			.count();
		return new SlaComplianceSummary(total, breached, total - breached);
	}

	public record SlaComplianceSummary(long total, long breached, long compliant) {
	}

}
