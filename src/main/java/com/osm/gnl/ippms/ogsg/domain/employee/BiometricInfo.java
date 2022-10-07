package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ippms_biometric_info")
@SequenceGenerator(name = "bioSeq", sequenceName = "ippms_biometric_info_seq", allocationSize = 1)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class BiometricInfo implements Serializable {
    private static final long serialVersionUID = -8152207096932364931L;

    @Id
    @GeneratedValue(generator = "bioSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "biometric_inst_id")
    private Long id;

    @Column(name = "bio_id")
    protected Long bioId;

    @Column(name = "business_client_inst_id")
    protected Long businessClientId;

    @Column(name = "first_name", length = 25, nullable = false)
    protected String firstName;

    @Column(name = "last_name", length = 25, nullable = false)
    protected String lastName;

    @Column(name = "middle_name", length = 25)
    protected String middleName;

    @Column(name = "registry_id")
    protected Long registryId;

    @Column(name = "agency_name", length = 25)
    protected String agencyName;

    @Column(name = "email", length = 40)
    @Email
    protected String email;

    @Column(name = "phone_number", length = 11)
    protected String phoneNumber;

    @Column(name = "bvn", length = 11)
    protected String bvnNumber;

    @Column(name = "user_id", length = 80)
    protected String userId;

    @Column(name = "gender", length = 6)
    protected String gender;

    @Column(name = "employee_agency", length = 40)
    protected String employeeAgency;

    @Column(name = "employee_id", length = 20, nullable = false, unique = true)
    protected String employeeId;

    @Column(name = "reason_for_delete", length = 80)
    protected String reasonForDelete;

    @Column(name="has_biometric", columnDefinition = "integer default 0")
    protected int hasBiometric;

    @Column(name="has_signature", columnDefinition = "integer default 0")
    protected int hasSignature;

    @Column(name="has_passport", columnDefinition = "integer default 0")
    protected int hasPassport;

    @Column(name = "username", length = 40)
    protected String username;

    @Column(name="password")
    protected String password;

    @Column(name = "registrars", length = 40)
    protected String registrars;

    @Column(name = "user_roles", length = 40)
    protected String userRoles;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name = "profile_picture")
    protected byte[] profilePicture;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name = "signature")
    protected byte[] signature;

    @Column(name = "role", length = 80)
    protected String role;

    @Column(name="is_deleted", columnDefinition = "integer default 0")
    protected int deleted;

    @Column(name="deleted_by")
    protected String deletedBy;

    @Column(name="date_deleted")
    protected String dateDeleted;

    @Column(name="date_created")
    protected String dateCreated;

    @Column(name="counter")
    protected Long counter;

    @Column(name="verified_date")
    protected Timestamp lastModTs;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_inst_id")
    protected User verifiedBy;

    @Transient private String legacyId;
    @Transient private String verifiedByMsg;
    @Transient private String verifiedDateMsg;
    @Transient protected String empty;
    @Transient protected  String photoString;
    @Transient protected  String signatureString;
    @Transient protected String photoType;
    @Transient private boolean biometricDataExists;
    @Transient private boolean response;
    @Transient private Long parentId;
    @Transient private boolean updPixBind;
    @Transient private boolean usedLegacyId;
    @Transient private boolean hasLegacyId;

    public BiometricInfo(Long pId){
        this.id = pId;
    }

    public boolean isHasLegacyId(){
        hasLegacyId = IppmsUtils.isNotNullOrEmpty(this.legacyId);
        return hasLegacyId;
    }


    public boolean isNewEntity(){
         return this.id == null;
     }
}
