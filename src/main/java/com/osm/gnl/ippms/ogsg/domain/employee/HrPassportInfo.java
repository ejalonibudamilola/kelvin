/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "ippms_employee_passport")
@SequenceGenerator(name = "passportSeq", sequenceName = "ippms_employee_passport_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class HrPassportInfo extends AbstractControlEntity implements Serializable
{
	 
	private static final long serialVersionUID = -394189373050655267L;
	
	@Id
	@GeneratedValue(generator = "passportSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "passport_inst_id", nullable = false, unique = true)
	private Long id;
	 
	@Basic(fetch=FetchType.EAGER)
	@Lob @Column(name="photo")
	private byte[] photo;

	@OneToOne()
	@JoinColumn(name = "employee_inst_id",  unique = true)
	private Employee employee;

	@OneToOne()
	@JoinColumn(name = "pensioner_inst_id",  unique = true)
	private Pensioner pensioner;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;
	
	@Column(name = "photo_type", nullable = false, length = 50)
	private String photoType;

	@Transient private MultipartFile file;

	@Transient private String content;

	@Transient private String filename;
	
	public HrPassportInfo(byte[] photo) {
		this.photo = photo;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
    	return this.id == null;
    }

	
}