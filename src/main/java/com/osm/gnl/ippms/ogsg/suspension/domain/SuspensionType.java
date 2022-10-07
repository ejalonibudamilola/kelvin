package com.osm.gnl.ippms.ogsg.suspension.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.ON;


@Entity
@Table(name = "ippms_suspension_type")
@SequenceGenerator(name = "suspendTypeSeq", sequenceName = "ippms_suspension_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SuspensionType extends AbstractDescControlEntity
{

	private static final long serialVersionUID = -4548141638899302704L;

	@Id
	@GeneratedValue(generator = "suspendTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "suspension_type_inst_id", nullable = false, unique = true)
	private Long id;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "pay_percentage_ind" , columnDefinition = "integer default '0'")
	private int payPercentageInd;

	@Column(name = "suspension_code" , columnDefinition = "integer default '0'")
	private int suspensionCode;

	@Column(name = "i_am_alive_ind" , columnDefinition = "integer default '0'")
	private int iAmAliveSusTypeInd;

	@Transient private boolean payPercentage;

	@Transient private String payPercentageStr;

	public SuspensionType(Long pId) {
		this.id = pId;
	}

	public SuspensionType(Long pId, String pName) {
		this.id = pId;
		this.name = pName;
	}

	public boolean isInterdiction(){
		return this.payPercentageInd == ON;
	}

	public String getPayPercentageStr() {
		if(this.payPercentageInd == 1){
			payPercentageStr = "Yes";
		}else{
			payPercentageStr = "No";
		}
		return payPercentageStr;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}
}