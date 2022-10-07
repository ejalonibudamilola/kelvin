package com.osm.gnl.ippms.ogsg.domain.promotion;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "ippms_promotion_tracker")
@SequenceGenerator(name = "promotionSeq", sequenceName = "ippms_promotion_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PromotionTracker extends AbstractPromotionTracker {
	private static final long serialVersionUID = 2778743846111277854L;

	@Id
	@GeneratedValue(generator = "promotionSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "promotion_inst_id", nullable = false, unique = true)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pension_inst_id")
	private Pensioner pensioner;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;


	public PromotionTracker(Long pId) {
		this.id = pId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PromotionTracker)) return false;
		PromotionTracker that = (PromotionTracker) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(employee, that.employee);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, employee);
	}
	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}


}