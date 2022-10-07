package com.osm.gnl.ippms.ogsg.auth.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.PasswordResetBean;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.exception.UserAuthenticationException;
import com.osm.gnl.ippms.ogsg.auth.repository.IUserRepository;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.ON;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

//	@Value("${use.password-timer}")
//	private boolean passwordTimer;
	private final IUserRepository userRepo;
	private final GenericService genericService;

	@Autowired
	public UserDetailsServiceImpl(final IUserRepository userRepo, final GenericService genericService) {
		this.userRepo = userRepo;
		this.genericService = genericService;
	}


	@SneakyThrows
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(final String pUsername) throws UsernameNotFoundException,UserAuthenticationException {
		User user;
		try {
			user = userRepo.findByUsername(pUsername);
			if (!user.isNewEntity() && user.isChgPassword()) {
				PasswordResetBean passwordResetBean = userRepo.loadPasswordResetBean(user.getPasswordResetId());

				if (passwordHasExpired(passwordResetBean)) {
					user.setPasswordExpired(ON);
				} else {
					user.setPasswordExpired(IConstants.OFF);
				}
			}
			//Treat the Login Thingy..
			if (!user.isNewEntity() && !user.getRole().getBusinessClient().isExecutive())
			user = checkIfLoginAllowed(user);
		}catch (Exception nre){

            throw new UsernameNotFoundException("User "+pUsername+" Does Not Exist.");
	    }
		return user;
	}

	private User checkIfLoginAllowed(User user) {
		if(user.isAllowWeekendLogin() || user.getRole().isSuperAdmin())
		   return user;
		if(PayrollUtils.isWeekend(LocalDate.now())){
			user.setAccountLocked(ON);
			return user;
		}
          if(!this.allowedTimeMet(user.getRole().getBusinessClient().getId()))
				   user.setAccountLocked(ON);

		return user;
	}


	private boolean passwordHasExpired(PasswordResetBean passwordResetBean) {

//		if(!passwordTimer)
//			return false;
		LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime passwordTime = LocalDateTime.from(passwordResetBean.getExpirationDate().toLocalDateTime());

		long minutes = Duration.between(localDateTime,passwordTime).toMinutes();

		if(minutes >= 0 && minutes <= 7)
			return false;
		return true;

	}
	private boolean allowedTimeMet(Long pBizId) {
		ConfigurationBean configurationBean;

		try {
			configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId",pBizId));
			if(configurationBean.getCutOffStartTime() != null || configurationBean.getCutOffEndTime() != null){

                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
                String currentTimeStr = formatter.format(LocalDateTime.now());

				int currentTime = Integer.parseInt(currentTimeStr.substring(0,currentTimeStr.indexOf(":"))
						+currentTimeStr.substring(currentTimeStr.indexOf(":") + 1,currentTimeStr.lastIndexOf(":")));
				if(IppmsUtils.isNotNullOrEmpty(configurationBean.getCutOffStartTime() )){
					int startTime = Integer.parseInt(configurationBean.getCutOffStartTime().substring(0,configurationBean.getCutOffStartTime().indexOf(":"))
							+configurationBean.getCutOffStartTime().substring(configurationBean.getCutOffStartTime().indexOf(":") + 1,configurationBean.getCutOffStartTime().lastIndexOf(":")));
					if(currentTime < startTime)
						return false;
				}
				if(IppmsUtils.isNotNullOrEmpty(configurationBean.getCutOffEndTime())){
					int endTime = Integer.parseInt(configurationBean.getCutOffStartTime().substring(0,configurationBean.getCutOffStartTime().indexOf(":"))
							+configurationBean.getCutOffStartTime().substring(configurationBean.getCutOffStartTime().indexOf(":") + 1,configurationBean.getCutOffStartTime().lastIndexOf(":")));
					if(currentTime > endTime)
						return false;
				}


			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


}
