package model.dto;

import model.Car;

import java.util.List;

public class ResultsDTO {

    private List<Car> resultsPage;
    private Integer currentPage;
    private Integer totalResults;
    private Integer totalPages;

    public ResultsDTO() {

    }

    public ResultsDTO(List<Car> allResults, int pageSize) {
        // Vrati prvu stranu
        this.currentPage = 1;
        this.resultsPage = allResults.subList(0, pageSize);
        this.totalResults = allResults.size();
        this.totalPages = (int) Math.ceil(this.totalResults / pageSize);
    }

    public ResultsDTO(List<Car> allResults, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        this.currentPage = page;
        this.resultsPage = allResults.subList(start, start+pageSize);
        this.totalResults = allResults.size();
        this.totalPages = (int) Math.ceil(this.totalResults / pageSize);
    }

    public List<Car> getResultsPage() {
        return resultsPage;
    }

    public void setResultsPage(List<Car> resultsPage) {
        this.resultsPage = resultsPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
