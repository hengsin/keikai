package io.keikai.range.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import io.keikai.model.SBook;
import io.keikai.model.SBookSeries;
import io.keikai.model.SSheet;
import io.keikai.model.SheetRegion;
import io.keikai.model.impl.CellAttribute;
import io.keikai.model.sys.dependency.ObjectRef;
import io.keikai.model.sys.dependency.Ref;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Dennis
 * @since 3.5.0
 */
/*package*/ class RefNotifyContentChangeHelper extends RefHelperBase implements Serializable {
	private static final long serialVersionUID = -816763214525819388L;

	private static final Logger _logger = Logger.getLogger(RefNotifyContentChangeHelper.class.getName());
			
	private NotifyChangeHelper _notifyHelper = new NotifyChangeHelper();
	public RefNotifyContentChangeHelper(SBookSeries bookSeries) {
		super(bookSeries);
	}
	@Deprecated
	public void notifyContentChange(Ref notify) {
		notifyContentChange(notify, CellAttribute.ALL);
	}
	//ZSS-939
	//@since 3.8.0
	public void notifyContentChange(Ref notify, CellAttribute cellAttr) {
		if (notify.getType() == Ref.RefType.CELL || notify.getType() == Ref.RefType.AREA) {
			handleAreaRef(notify, cellAttr); //ZSS-939
		} else if (notify.getType() == Ref.RefType.OBJECT) {
			if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.CHART){
				handleChartRef((ObjectRef)notify);
			}else if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.DATA_VALIDATION){
				handleDataValidationRef((ObjectRef)notify);
			}else if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.AUTO_FILTER){ // ZSS-555
				handleAutoFilterRef((ObjectRef)notify);
			}
		} else {// TODO another

		}
	}
	@Deprecated
	public void notifyContentChange(Set<Ref> notifySet) {
		notifyContentChange(notifySet, CellAttribute.ALL);
	}
	//ZSS-939
	//@since 3.8.0
	public void notifyContentChange(Set<Ref> notifySet, CellAttribute cellAttr) {
		Map<String,Ref> chartDependents  = new LinkedHashMap<String, Ref>();
		Map<String,Ref> validationDependents  = new LinkedHashMap<String, Ref>();	
		
		// clear formula cache
		for (Ref notify : notifySet) {
			if(_logger.isLoggable(Level.INFO)){
				_logger.info("Notify Dependent Change : "+notify+" with attibute "+ cellAttr);
			}
			//clear the dependent's formula cache since the precedent is changed.
			if (notify.getType() == Ref.RefType.CELL || notify.getType() == Ref.RefType.AREA) {
				handleAreaRef(notify, cellAttr); //ZSS-939
			} else if (notify.getType() == Ref.RefType.OBJECT) {
				if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.CHART){
					chartDependents.put(((ObjectRef)notify).getObjectIdPath()[0], notify);
				}else if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.DATA_VALIDATION){
					validationDependents.put(((ObjectRef)notify).getObjectIdPath()[0], notify);
				}else if(((ObjectRef)notify).getObjectType()== ObjectRef.ObjectType.AUTO_FILTER){
					handleAutoFilterRef((ObjectRef)notify);
				}
			} else {// TODO another

			}
		}
		
		for (Ref notify : chartDependents.values()) {
			handleChartRef((ObjectRef)notify);
		}
		for (Ref notify : validationDependents.values()) {
			handleDataValidationRef((ObjectRef)notify);
			//ZSS-834
			//20141124, henrichen: each handleDataValidationRef(notify) will 
			//  handle ALL validations on a book for rendering; thus no need
			//  to iterate each validation. break to avoid unnecessary handling.
			break;
		}	
	}

	private void handleChartRef(ObjectRef notify) {
		SBook book = bookSeries.getBook(notify.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(notify.getSheetName());
		if(sheet==null) return;
		String[] ids = notify.getObjectIdPath();
		_notifyHelper.notifyChartChange(sheet,ids[0]);
				
	}
	
	private void handleDataValidationRef(ObjectRef notify) {
		SBook book = bookSeries.getBook(notify.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(notify.getSheetName());
		if(sheet==null) return;
		String[] ids = notify.getObjectIdPath();
		_notifyHelper.notifyDataValidationChange(sheet,ids[0]);
	}

	// ZSS-555
	private void handleAutoFilterRef(ObjectRef notify) {
		SBook book = bookSeries.getBook(notify.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(notify.getSheetName());
		if(sheet==null) return;
		_notifyHelper.notifySheetAutoFilterChange(sheet, null); //ZSS-988
	}

	private void handleAreaRef(Ref notify, CellAttribute cellAttr) {
		SBook book = bookSeries.getBook(notify.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(notify.getSheetName());
		if(sheet==null) return;
		final int row1 = notify.getRow();
		final int row2 = notify.getLastRow();
		final int col1 = notify.getColumn();
		final int col2 = notify.getLastColumn();
		_notifyHelper.notifyCellChange(
			new SheetRegion(sheet,row1,col1,row2,col2), cellAttr); //ZSS-939
	}
}
