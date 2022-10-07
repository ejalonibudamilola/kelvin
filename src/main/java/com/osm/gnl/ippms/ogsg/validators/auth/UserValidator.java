package com.osm.gnl.ippms.ogsg.validators.auth;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator extends BaseValidator
{

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserValidator(GenericService genericService) {
		super(genericService);
	}

	/**
	 * Can this {@link Validator} {@link #validate(Object, Errors) validate}
	 * instances of the supplied {@code clazz}?
	 * <p>This method is <i>typically</i> implemented like so:
	 * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
	 * (Where {@code Foo} is the class (or superclass) of the actual
	 * object instance that is to be {@link #validate(Object, Errors) validated}.)
	 *
	 * @param clazz the {@link Class} that this {@link Validator} is
	 *              being asked if it can {@link #validate(Object, Errors) validate}
	 * @return {@code true} if this {@link Validator} can indeed
	 * {@link #validate(Object, Errors) validate} instances of the
	 * supplied {@code clazz}
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	public void validate(Object pTarget, Errors pErrors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "userName",
				"required.userName", "Username is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "password",
				"required.password", "Password is required.");

		
	}
	
	/**
	   * 
	   * @param pTarget
	   * @param pErrors
	   * 
	   * @deprecated this method was used to validate for the old security platform
	   */
	@Deprecated
	public void validateForChangedPassword(Object pTarget, Errors pErrors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newPassword",
				"required.newPassword", "New Password is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "confirmNewPassword",
				"required.confirmNewPassword", "Confirm Password is required.");
		if (pErrors.getErrorCount() < 1) {
			User login = (User) pTarget;
			if (login.getNewPassword().equalsIgnoreCase(login.getOldPassword())) {
				pErrors.rejectValue("newPassword", "ChangeNotDone",
						"Old password is the same as New password!");
			} else if (!login.getNewPassword().equalsIgnoreCase(
					login.getConfirmNewPassword())) {
				pErrors.rejectValue("newPassword", "PasswordMismatch",
						"New password mismatch. Please confirm new password");
			}
		}
	}
	
	
	public void validatePasswordChange(User pLogin, Errors pErrors) throws Exception {
			ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newPassword", "required.newPassword", "New Password is required.");

		    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "confirmNewPassword", "required.confirmNewPassword", "Confirm Password is required.");
		    
		    if( !pErrors.hasErrors() ) {
		    	//if the user is changing password because of a change 
		        //in the security platform of the application then
		        //the same password can be used.

		    	
		    	//get inserted values
		    	final String wNewPass = pLogin.getNewPassword();
		    	final String wConfirmPass = pLogin.getConfirmNewPassword();
		    	
		    	//variables for encrypted values
		    	String wEncryptedPassToBeCheckedAgainstOld = null;
		    	
		    	//if the inserted passwords don't match...case insensitive check!!!
		    	if( !wNewPass.equals( wConfirmPass ) ) {
		    		pErrors.rejectValue( "newPassword", "PasswordMismatch", "New password mismatch. Please confirm new password" );
		    		return;
		    	}
				if(wNewPass.length() < 8){
					pErrors.rejectValue("newPassword", "ChangeNotDone", "Minimum length for password is 8.");
				}
				if(StringUtils.containsNone(wNewPass, PassPhrase.numbers)){
					pErrors.rejectValue("newPassword", "ChangeNotDone", "Password must contain at least 1 Number [0-9]");

				}
				if(StringUtils.containsNone(wNewPass,PassPhrase.upperCases)){
					pErrors.rejectValue("newPassword", "ChangeNotDone", "Password must contain at least 1 Upper Case Alphabet. [A-Z]");
				}
				if(StringUtils.containsNone(wNewPass,PassPhrase.special)){
					pErrors.rejectValue("newPassword", "ChangeNotDone", "Password must contain at least 1 Special Character from : "+new String(PassPhrase.special));
				}
				if(pErrors.hasErrors())
					return;
				wEncryptedPassToBeCheckedAgainstOld = this.passwordEncoder.encode(wNewPass);


		        //check if the previous password is the same as the new one
		      	if( pLogin.getPassword().equalsIgnoreCase( wEncryptedPassToBeCheckedAgainstOld ) ) {
		      		pErrors.rejectValue("newPassword", "ChangeNotDone", "Old password is the same as New password!");
			    		return;
		      	}
		      	
		      	pLogin.setPassword(wEncryptedPassToBeCheckedAgainstOld);
		        
		    }//end if( !pErrors.hasErrors() )
		}


}