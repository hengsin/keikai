package io.keikai.model.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import io.keikai.model.SBook;
import io.keikai.model.SBookSeries;
import io.keikai.model.SCell;
import io.keikai.model.SChart;
import io.keikai.model.SConditionalFormattingRule;
import io.keikai.model.SDataValidation;
import io.keikai.model.SRow;
import io.keikai.model.SSheet;
import io.keikai.model.sys.dependency.ConditionalRef;
import io.keikai.model.sys.dependency.ObjectRef;
import io.keikai.model.sys.dependency.ObjectRef.ObjectType;
import io.keikai.model.sys.dependency.Ref;
import io.keikai.model.sys.dependency.Ref.RefType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author dennis
 * @since 3.5.0
 */
/*package*/ class FormulaCacheClearHelper implements Serializable{
	private static final long serialVersionUID = 8757672812896139208L;

	private final SBookSeries _bookSeries;
	
	private static final Logger logger = Logger.getLogger(FormulaCacheClearHelper.class.getName());
	
	public FormulaCacheClearHelper(SBookSeries bookSeries) {
		this._bookSeries = bookSeries;
	}

	public void clear(Set<Ref> refs) {
		// clear formula cache
		for (Ref ref : refs) {
			if(logger.isLoggable(Level.INFO)){
				logger.info("Clear Formula Cache: "+ref);
			}
			//clear the dependent's formula cache since the precedent is changed.
			if (ref.getType() == RefType.CELL || ref.getType() == RefType.AREA) {
				handleAreaRef(ref);
			} else if (ref.getType() == RefType.OBJECT) {
				if(((ObjectRef)ref).getObjectType()==ObjectType.CHART){
					handleChartRef((ObjectRef)ref);
				}else if(((ObjectRef)ref).getObjectType()==ObjectType.DATA_VALIDATION){
					handleDataValidationRef((ObjectRef)ref);
				}
			} else if (ref.getType() == RefType.CONDITIONAL) { //ZSS-1251
				handleConditionalRef((ConditionalRef)ref);
			} else {// TODO another
			}
		}
	}
	private void handleChartRef(ObjectRef ref) {
		SBook book = _bookSeries.getBook(ref.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(ref.getSheetName());
		if(sheet==null) return;
		String[] ids = ref.getObjectIdPath();
		SChart chart = sheet.getChart(ids[0]);
		if(chart!=null){
			chart.getData().clearFormulaResultCache();
		}
	}
	//ZSS-1251
	private void handleConditionalRef(ConditionalRef ref) {
		SBook book = _bookSeries.getBook(ref.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(ref.getSheetName());
		if(sheet==null) return;
		int id = ref.getConditionalId();
		ConditionalFormattingImpl cfmt = (ConditionalFormattingImpl)((AbstractSheetAdv)sheet).getConditionalFormatting(id);
		for (SConditionalFormattingRule rule0 : cfmt.getRules()) {
			final ConditionalFormattingRuleImpl rule = (ConditionalFormattingRuleImpl) rule0;
			switch(rule.getType()) {
			case CELL_IS:
			case BEGINS_WITH:
			case ENDS_WITH:
			case CONTAINS_TEXT:
			case NOT_CONTAINS_TEXT:
			case CONTAINS_BLANKS:
			case NOT_CONTAINS_BLANKS:
			case CONTAINS_ERRORS:
			case NOT_CONTAINS_ERRORS:
			case EXPRESSION:
				if (rule.getRuleInfo1() != null) {
					rule.getRuleInfo1().clearCacheMap();
					if (rule.getRuleInfo2() != null) {
						rule.getRuleInfo2().clearCacheMap();
					}
				} else {
					rule.clearFormulaResultCache();
				}
				break;
				
			case ABOVE_AVERAGE:
			case COLOR_SCALE:
			case DATA_BAR:
			case DUPLICATE_VALUES:
			case ICON_SET:
			case TIME_PERIOD:
			case TOP_10:
			case UNIQUE_VALUES:
				rule.clearFormulaResultCache();
				break;
			}
		}
	}
	
	private void handleDataValidationRef(ObjectRef ref) {
		SBook book = _bookSeries.getBook(ref.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(ref.getSheetName());
		if(sheet==null) return;
		String[] ids = ref.getObjectIdPath();
		SDataValidation validation = sheet.getDataValidation(ids[0]);
		if(validation!=null){
			validation.clearFormulaResultCache();
		}
	}

	private void handleAreaRef(Ref ref) {
		SBook book = _bookSeries.getBook(ref.getBookName());
		if(book==null) return;
		SSheet sheet = book.getSheetByName(ref.getSheetName());
		if(sheet==null) return;
		
		boolean wholeRow = ref.getColumn()==0 && ref.getLastColumn()>=book.getMaxColumnIndex();
		boolean wholeColumn = ref.getRow()==0 && ref.getLastRow()>=book.getMaxRowIndex();
		boolean wholeSheet = wholeRow && wholeColumn;
		
		if(wholeSheet){
			Iterator<SRow> rows = sheet.getRowIterator();
			while(rows.hasNext()){
				Iterator<SCell> cells = sheet.getCellIterator(rows.next().getIndex());
				while(cells.hasNext()){
					cells.next().clearFormulaResultCache();
				}
			}
		}else if(wholeRow){
			//from column 0 to max
			for(int r = ref.getRow();r<=ref.getLastRow();r++){
				Iterator<SCell> cells = sheet.getCellIterator(r);
				while(cells.hasNext()){
					cells.next().clearFormulaResultCache();
				}
			}
		}else if(wholeColumn){
			//from row 0 to max
			Iterator<SRow> rows = sheet.getRowIterator();
			while(rows.hasNext()){
				int r = rows.next().getIndex();
				for(int c = ref.getColumn();c<=ref.getLastColumn();c++){
					SCell cell = ((AbstractSheetAdv)sheet).getCell(r,c,false);
					if(cell!=null){
						cell.clearFormulaResultCache();
					}
				}
			}
		}else{
			for(int r = ref.getRow();r<=ref.getLastRow();r++){
				for(int c = ref.getColumn();c<=ref.getLastColumn();c++){
					SCell cell = ((AbstractSheetAdv)sheet).getCell(r,c,false);
					if(cell!=null){
						cell.clearFormulaResultCache();
					}
				}
			}
		}
	}
}
