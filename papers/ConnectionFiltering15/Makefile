MAIN    = paper

${MAIN}.pdf: *.tex Makefile *.bib
	pdflatex ${MAIN}
	bibtex ${MAIN}
	pdflatex ${MAIN}
	pdflatex ${MAIN}

view: ${MAIN}.pdf
	open ${MAIN}.pdf

clean:
	@rm -f *.dvi *.aux *.bbl *.blg *.ps *.log *.toc *~ ${MAIN}.pdf

spotless: clean
	@rm -f *.dvi *.aux *.bbl *.blg *.ps *.pdf *.log *.toc *~
