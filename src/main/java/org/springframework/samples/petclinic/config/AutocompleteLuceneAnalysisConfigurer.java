package org.springframework.samples.petclinic.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class AutocompleteLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer( "autocomplete_indexing" ).custom()
            .tokenizer( WhitespaceTokenizerFactory.class )
            // Lowercase all characters
            .tokenFilter( LowerCaseFilterFactory.class )
            // Replace accented characters by their simpler counterpart (è => e, etc.)
            .tokenFilter( ASCIIFoldingFilterFactory.class )
            // Generate prefix tokens
            .tokenFilter( EdgeNGramFilterFactory.class )
            .param( "minGramSize", "1" )
            .param( "maxGramSize", "10" );
        // Same as "autocomplete-indexing", but without the edge-ngram filter
        context.analyzer( "autocomplete_search" ).custom()
            .tokenizer( WhitespaceTokenizerFactory.class )
            // Lowercase all characters
            .tokenFilter( LowerCaseFilterFactory.class )
            // Replace accented characters by their simpler counterpart (è => e, etc.)
            .tokenFilter( ASCIIFoldingFilterFactory.class );
    }
}

