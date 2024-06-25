package io.keikai.app.impl;

import java.io.IOException;
import java.io.Serializable;

import io.keikai.api.model.Book;
import org.zkoss.lang.Library;
import io.keikai.app.BookManager;
import io.keikai.app.BookInfo;
import io.keikai.app.BookRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BookManagerImpl implements BookManager, Serializable {
	private static final long serialVersionUID = -2811375696946379394L;
	private static final Logger logger = Logger.getLogger(BookManagerImpl.class.getName());
	protected BookRepository repo;
	
	@Override
	public Book readBook(BookInfo info) throws IOException {
		return repo.load(info);
	}

	@Override
	public BookInfo updateBook(BookInfo info, Book book) throws IOException {
		return repo.save(info, book);
	}

	@Override
	public BookInfo saveBook(BookInfo info, Book book) throws IOException {
			BookInfo newInfo = repo.saveAs(info.getName(), book);
			readBook(newInfo);
			return newInfo;
	}

	@Override
	public void deleteBook(BookInfo info) throws IOException {
		repo.delete(info);
	}

	@Override
	public void detachBook(BookInfo info) throws IOException {}
	
	@Override
	public boolean isBookAttached(BookInfo info) {
		return false;
	}
	
	@Override
	public void saveAll() throws IOException {}
	
	// ======================= Singleton Implementation ======================
	private static BookManagerImpl bookManagerImpl;
	protected BookManagerImpl(BookRepository repo) {
		this.repo = repo;
	}
	public static BookManagerImpl getInstance(BookRepository repo) {
		if (bookManagerImpl == null) {
			String clz = Library.getProperty("io.keikai.app.BookManager.class");
			if(clz != null && Boolean.valueOf(Library.getProperty("zssapp.collaboration.disabled")) != Boolean.TRUE){
				try {
					bookManagerImpl = (BookManagerImpl) Class.forName(clz).getDeclaredConstructor(BookRepository.class).newInstance(repo);
				} catch(Exception e) {
					bookManagerImpl = new BookManagerImpl(repo);
					logger.log(Level.SEVERE, e.getMessage(), e);
				}			
			} else
				bookManagerImpl = new BookManagerImpl(repo);
		}
		return bookManagerImpl;
	}

	@Override
	public void shutdownAutoFileSaving() {}
}
