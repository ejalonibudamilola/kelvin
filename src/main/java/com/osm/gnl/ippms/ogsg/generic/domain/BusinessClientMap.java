package com.osm.gnl.ippms.ogsg.generic.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "ippms_bus_client_login_map")
@SequenceGenerator(name = "busClientLogMapSeq", sequenceName = "ippms_bus_client_login_map_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BusinessClientMap {

	@Id
	@GeneratedValue(generator = "busClientLogMapSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "bus_client_log_map_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "business_client_inst_id")
	private BusinessClient businessClient;
	@ManyToOne
	@JoinColumn(name = "user_inst_id")
	private User user;


	public boolean isNewEntity() {
		return this.id == null;
	}
}