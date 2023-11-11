package com.yourrents.services.geodata.util.search;

public class PaginationUtils {

	public static int lastPage(int totNumRecords, int pageSize) {
		return (int) Math.ceil((double) totNumRecords / pageSize) - 1;
	}

	public static int numOfPages(int totNumRecords, int pageSize) {
		return lastPage(totNumRecords, pageSize) + 1;
	}


	public static int numRecordsInPage(int totNumRecords, int pageSize, int pageNumber) {
		return Math.min(pageSize, totNumRecords - (pageNumber * pageSize));
	}

}
