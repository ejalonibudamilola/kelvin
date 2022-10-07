package com.osm.gnl.ippms.ogsg.taglibrary;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

 
@Service
public class GNLCopyrightTagLibrary implements Serializable, Tag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1511397100241604577L;
	private final static Logger logger = LoggerFactory.getLogger( GNLCopyrightTagLibrary.class );
	private static final int DEFAULT_START_YEAR = 2010;
	private static final String HTML_COPYRIGHT = "&copy;";
	
	private PageContext fContext = null;
	private Tag parent = null;
	
	private Integer startYear = null;
	private Integer endYear = null;
	
	
	@Override
	public void setPageContext(PageContext pc) {
		 this.fContext = pc;
		
	}

	@Override
	public void setParent(Tag t) {
		// TODO Auto-generated method stub
		parent = t;
	}

	@Override
	public Tag getParent() {
		 
		return this.parent;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			if( startYear == null || (startYear < 2000) ) {
				startYear = DEFAULT_START_YEAR;
			}
			endYear = Calendar.getInstance().get(Calendar.YEAR);
			if(startYear.equals(endYear)) {
				this.fContext.getOut().write( 
						new StringBuilder(  HTML_COPYRIGHT ).append( Calendar.getInstance().get( Calendar.YEAR ) )
							.append( " GNL Systems Ltd." ).toString()
					);
			}else {
				this.fContext.getOut().write( 
							new StringBuilder(  HTML_COPYRIGHT ).append( startYear )
								.append( "-" ).append( Calendar.getInstance().get( Calendar.YEAR ) )
								.append( " GNL Systems Ltd." ).toString()
						);
			}
		}catch(IOException ex) {
			logger.error( "An exception occured while creating GNL Systems Copyright tag", ex );
			throw new JspException( "An IOException occured." );
		}
		
		return Tag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
	 
		return Tag.EVAL_PAGE;
	}

	@Override
	public void release() {
		 this.fContext = null;
		 this.parent = null;
		
	}
	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
	}
}
