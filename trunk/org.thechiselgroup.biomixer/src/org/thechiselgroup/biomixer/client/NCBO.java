/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.biomixer.client;

public interface NCBO {

    String CONCEPT_ONTOLOGY_VERSION_ID = "ontologyVersionId";

    String CONCEPT_ONTOLOGY_ID = "ontologyId";

    String CONCEPT_ONTOLOGY_NAME = "ontologyName";

    String CONCEPT_NAME = "conceptName";

    String CONCEPT_SHORT_ID = "conceptIdShort";

    String CONCEPT_ID = "conceptId";

    String CONCEPT_CHILD_COUNT = "childCount";

    String CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS = "neighbours";

    String MAPPING_ID = "mappingId";

    String MAPPING_SOURCE = "mappingSource"; // uri

    String MAPPING_DESTINATION = "mappingDestination"; // uri

    String MAPPING_CREATION_DATE = "date"; // FIXME use better name once slot

    // resolver is more flexible

    String MAPPING_SOURCE_CONCEPT_NAME = "mappingSourceConceptName";

    String MAPPING_DESTINATION_CONCEPT_NAME = "mappingDestinationConceptName";

    String MAPPING_SOURCE_ONTOLOGY_NAME = "mappingSourceOntologyName";

    String MAPPING_DESTINATION_ONTOLOGY_NAME = "mappingDestinationOntologyName";

    String MAPPING_SOURCE_CONCEPT_ID = "mappingSourceConceptID";

    String MAPPING_DESTINATION_CONCEPT_ID = "mappingDestinationConceptID";

    String MAPPING_SOURCE_ONTOLOGY_ID = "mappingSourceOntologyID";

    String MAPPING_SOURCE_ONTOLOGY_VERSION_ID = "mappingSourceOntologyVersionID";

    String MAPPING_DESTINATION_ONTOLOGY_ID = "mappingDestinationOntologyID";

    String MAPPING_DESTINATION_ONTOLOGY_VERSION_ID = "mappingDestinationOntologyVersionID";

}
