package wf.garnier.spring.boot.test.ch5.ticket.ticket;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByTicketIdOrderByCreatedAtDesc(Long ticketId);

}
