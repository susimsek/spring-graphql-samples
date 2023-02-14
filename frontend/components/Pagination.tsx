import React, {useEffect} from "react";

import Pagination from "react-bootstrap/Pagination";

interface PaginationProps {
    itemsCount: number;
    itemsPerPage: number;
    currentPage: number;
    setCurrentPage: (page: number) => void;
    alwaysShown: boolean;
}

const PaginationComponent: React.FC<PaginationProps> = ({
                                                   itemsCount,
                                                   itemsPerPage,
                                                   currentPage,
                                                   setCurrentPage,
                                                   alwaysShown = true
                                               }) => {

    const pagesCount = Math.ceil(itemsCount / itemsPerPage);
    const isPaginationShown = alwaysShown ? true : pagesCount > 1;
    const isCurrentPageFirst = currentPage === 1;
    const isCurrentPageLast = currentPage === pagesCount;

    const changePage = (page: number) => {
        if (currentPage === page) return;
        setCurrentPage(page);
    };

    const onPageNumberClick = (page: number) => {
        changePage(page);
    };

    const onPreviousPageClick = () => {
        changePage(currentPage - 1);
    };

    const onNextPageClick = () => {
        changePage(currentPage + 1);
    };

    const setLastPageAsCurrent = () => {
        if (currentPage > pagesCount) {
            setCurrentPage(pagesCount);
        }
    };

    let isPageNumberOutOfRange: boolean;

    const pageNumbers = [...new Array(pagesCount)].map((_, index) => {
        const pageNumber = index + 1;
        const isPageNumberFirst = pageNumber === 1;
        const isPageNumberLast = pageNumber === pagesCount;
        const isCurrentPageWithinTwoPageNumbers =
            Math.abs(pageNumber - currentPage) <= 2;

        if (
            isPageNumberFirst ||
            isPageNumberLast ||
            isCurrentPageWithinTwoPageNumbers
        ) {
            isPageNumberOutOfRange = false;
            return (
                <Pagination.Item
                    key={pageNumber}
                    onClick={() => onPageNumberClick(pageNumber)}
                    active={pageNumber === currentPage}
                >
                    {pageNumber}
                </Pagination.Item>
            );
        }

        if (!isPageNumberOutOfRange) {
            isPageNumberOutOfRange = true;
            return <Pagination.Ellipsis key={pageNumber} className="muted" />;
        }

        return null;
    });

    useEffect(setLastPageAsCurrent, [pagesCount]);

    return (
        <>
            {isPaginationShown && (
                <Pagination size="lg" className="d-flex justify-content-center">
                    <Pagination.Prev
                        onClick={onPreviousPageClick}
                        disabled={isCurrentPageFirst}
                    />
                    {pageNumbers}
                    <Pagination.Next
                        onClick={onNextPageClick}
                        disabled={isCurrentPageLast}
                    />
                </Pagination>
            )}
        </>
    );
}

export default PaginationComponent;