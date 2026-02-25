package wf.garnier.spring.boot.test.ch5.ticket.agent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {

}
