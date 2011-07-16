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
package org.thechiselgroup.choosel.protovis.client;

/**
 * Miserables data set from protovis examples.
 * 
 * @see http://vis.stanford.edu/protovis/ex/miserables.js
 */
public class MiserablesData {

    public static class NovelCharacterNodeAdapter implements
            PVNodeAdapter<NovelCharacter> {

        public String getNodeName(NovelCharacter t) {
            return t.getCharacterName();
        }

        public Object getNodeValue(NovelCharacter t) {
            return null;
        }
    }

    public static class NovelCharacter {

        private String characterName;

        private int group;

        public NovelCharacter(String characterName, int group) {
            this.characterName = characterName;
            this.group = group;
        }

        public String getCharacterName() {
            return characterName;
        }

        public int getGroup() {
            return group;
        }

    }

    public final static NovelCharacter[] CHARACTERS = new NovelCharacter[] {
            new NovelCharacter("Myriel", 1), new NovelCharacter("Napoleon", 1),
            new NovelCharacter("Mlle. Baptistine", 1),
            new NovelCharacter("Mme. Magloire", 1),
            new NovelCharacter("Countess de Lo", 1),
            new NovelCharacter("Geborand", 1),
            new NovelCharacter("Champtercier", 1),
            new NovelCharacter("Cravatte", 1), new NovelCharacter("Count", 1),
            new NovelCharacter("Old Man", 1), new NovelCharacter("Labarre", 2),
            new NovelCharacter("Valjean", 2),
            new NovelCharacter("Marguerite", 3),
            new NovelCharacter("Mme. de R", 2),
            new NovelCharacter("Isabeau", 2), new NovelCharacter("Gervais", 2),
            new NovelCharacter("Tholomyes", 3),
            new NovelCharacter("Listolier", 3),
            new NovelCharacter("Fameuil", 3),
            new NovelCharacter("Blacheville", 3),
            new NovelCharacter("Favourite", 3),
            new NovelCharacter("Dahlia", 3), new NovelCharacter("Zephine", 3),
            new NovelCharacter("Fantine", 3),
            new NovelCharacter("Mme. Thenardier", 4),
            new NovelCharacter("Thenardier", 4),
            new NovelCharacter("Cosette", 5), new NovelCharacter("Javert", 4),
            new NovelCharacter("Fauchelevent", 0),
            new NovelCharacter("Bamatabois", 2),
            new NovelCharacter("Perpetue", 3),
            new NovelCharacter("Simplice", 2),
            new NovelCharacter("Scaufflaire", 2),
            new NovelCharacter("Woman 1", 2), new NovelCharacter("Judge", 2),
            new NovelCharacter("Champmathieu", 2),
            new NovelCharacter("Brevet", 2),
            new NovelCharacter("Chenildieu", 2),
            new NovelCharacter("Cochepaille", 2),
            new NovelCharacter("Pontmercy", 4),
            new NovelCharacter("Boulatruelle", 6),
            new NovelCharacter("Eponine", 4), new NovelCharacter("Anzelma", 4),
            new NovelCharacter("Woman 2", 5),
            new NovelCharacter("Mother Innocent", 0),
            new NovelCharacter("Gribier", 0),
            new NovelCharacter("Jondrette", 7),
            new NovelCharacter("Mme. Burgon", 7),
            new NovelCharacter("Gavroche", 8),
            new NovelCharacter("Gillenormand", 5),
            new NovelCharacter("Magnon", 5),
            new NovelCharacter("Mlle. Gillenormand", 5),
            new NovelCharacter("Mme. Pontmercy", 5),
            new NovelCharacter("Mlle. Vaubois", 5),
            new NovelCharacter("Lt. Gillenormand", 5),
            new NovelCharacter("Marius", 8),
            new NovelCharacter("Baroness T", 5),
            new NovelCharacter("Mabeuf", 8), new NovelCharacter("Enjolras", 8),
            new NovelCharacter("Combeferre", 8),
            new NovelCharacter("Prouvaire", 8),
            new NovelCharacter("Feuilly", 8),
            new NovelCharacter("Courfeyrac", 8),
            new NovelCharacter("Bahorel", 8), new NovelCharacter("Bossuet", 8),
            new NovelCharacter("Joly", 8), new NovelCharacter("Grantaire", 8),
            new NovelCharacter("Mother Plutarch", 9),
            new NovelCharacter("Gueulemer", 4), new NovelCharacter("Babet", 4),
            new NovelCharacter("Claquesous", 4),
            new NovelCharacter("Montparnasse", 4),
            new NovelCharacter("Toussaint", 5),
            new NovelCharacter("Child 1", 10),
            new NovelCharacter("Child 2", 10), new NovelCharacter("Brujon", 4),
            new NovelCharacter("Mme. Hucheloup", 8) };

    public static final Link[] LINKS = new Link[] { new Link(1, 0, 1),
            new Link(2, 0, 8), new Link(3, 0, 10), new Link(3, 2, 6),
            new Link(4, 0, 1), new Link(5, 0, 1), new Link(6, 0, 1),
            new Link(7, 0, 1), new Link(8, 0, 2), new Link(9, 0, 1),
            new Link(11, 10, 1), new Link(11, 3, 3), new Link(11, 2, 3),
            new Link(11, 0, 5), new Link(12, 11, 1), new Link(13, 11, 1),
            new Link(14, 11, 1), new Link(15, 11, 1), new Link(17, 16, 4),
            new Link(18, 16, 4), new Link(18, 17, 4), new Link(19, 16, 4),
            new Link(19, 17, 4), new Link(19, 18, 4), new Link(20, 16, 3),
            new Link(20, 17, 3), new Link(20, 18, 3), new Link(20, 19, 4),
            new Link(21, 16, 3), new Link(21, 17, 3), new Link(21, 18, 3),
            new Link(21, 19, 3), new Link(21, 20, 5), new Link(22, 16, 3),
            new Link(22, 17, 3), new Link(22, 18, 3), new Link(22, 19, 3),
            new Link(22, 20, 4), new Link(22, 21, 4), new Link(23, 16, 3),
            new Link(23, 17, 3), new Link(23, 18, 3), new Link(23, 19, 3),
            new Link(23, 20, 4), new Link(23, 21, 4), new Link(23, 22, 4),
            new Link(23, 12, 2), new Link(23, 11, 9), new Link(24, 23, 2),
            new Link(24, 11, 7), new Link(25, 24, 13), new Link(25, 23, 1),
            new Link(25, 11, 12), new Link(26, 24, 4), new Link(26, 11, 31),
            new Link(26, 16, 1), new Link(26, 25, 1), new Link(27, 11, 17),
            new Link(27, 23, 5), new Link(27, 25, 5), new Link(27, 24, 1),
            new Link(27, 26, 1), new Link(28, 11, 8), new Link(28, 27, 1),
            new Link(29, 23, 1), new Link(29, 27, 1), new Link(29, 11, 2),
            new Link(30, 23, 1), new Link(31, 30, 2), new Link(31, 11, 3),
            new Link(31, 23, 2), new Link(31, 27, 1), new Link(32, 11, 1),
            new Link(33, 11, 2), new Link(33, 27, 1), new Link(34, 11, 3),
            new Link(34, 29, 2), new Link(35, 11, 3), new Link(35, 34, 3),
            new Link(35, 29, 2), new Link(36, 34, 2), new Link(36, 35, 2),
            new Link(36, 11, 2), new Link(36, 29, 1), new Link(37, 34, 2),
            new Link(37, 35, 2), new Link(37, 36, 2), new Link(37, 11, 2),
            new Link(37, 29, 1), new Link(38, 34, 2), new Link(38, 35, 2),
            new Link(38, 36, 2), new Link(38, 37, 2), new Link(38, 11, 2),
            new Link(38, 29, 1), new Link(39, 25, 1), new Link(40, 25, 1),
            new Link(41, 24, 2), new Link(41, 25, 3), new Link(42, 41, 2),
            new Link(42, 25, 2), new Link(42, 24, 1), new Link(43, 11, 3),
            new Link(43, 26, 1), new Link(43, 27, 1), new Link(44, 28, 3),
            new Link(44, 11, 1), new Link(45, 28, 2), new Link(47, 46, 1),
            new Link(48, 47, 2), new Link(48, 25, 1), new Link(48, 27, 1),
            new Link(48, 11, 1), new Link(49, 26, 3), new Link(49, 11, 2),
            new Link(50, 49, 1), new Link(50, 24, 1), new Link(51, 49, 9),
            new Link(51, 26, 2), new Link(51, 11, 2), new Link(52, 51, 1),
            new Link(52, 39, 1), new Link(53, 51, 1), new Link(54, 51, 2),
            new Link(54, 49, 1), new Link(54, 26, 1), new Link(55, 51, 6),
            new Link(55, 49, 12), new Link(55, 39, 1), new Link(55, 54, 1),
            new Link(55, 26, 21), new Link(55, 11, 19), new Link(55, 16, 1),
            new Link(55, 25, 2), new Link(55, 41, 5), new Link(55, 48, 4),
            new Link(56, 49, 1), new Link(56, 55, 1), new Link(57, 55, 1),
            new Link(57, 41, 1), new Link(57, 48, 1), new Link(58, 55, 7),
            new Link(58, 48, 7), new Link(58, 27, 6), new Link(58, 57, 1),
            new Link(58, 11, 4), new Link(59, 58, 15), new Link(59, 55, 5),
            new Link(59, 48, 6), new Link(59, 57, 2), new Link(60, 48, 1),
            new Link(60, 58, 4), new Link(60, 59, 2), new Link(61, 48, 2),
            new Link(61, 58, 6), new Link(61, 60, 2), new Link(61, 59, 5),
            new Link(61, 57, 1), new Link(61, 55, 1), new Link(62, 55, 9),
            new Link(62, 58, 17), new Link(62, 59, 13), new Link(62, 48, 7),
            new Link(62, 57, 2), new Link(62, 41, 1), new Link(62, 61, 6),
            new Link(62, 60, 3), new Link(63, 59, 5), new Link(63, 48, 5),
            new Link(63, 62, 6), new Link(63, 57, 2), new Link(63, 58, 4),
            new Link(63, 61, 3), new Link(63, 60, 2), new Link(63, 55, 1),
            new Link(64, 55, 5), new Link(64, 62, 12), new Link(64, 48, 5),
            new Link(64, 63, 4), new Link(64, 58, 10), new Link(64, 61, 6),
            new Link(64, 60, 2), new Link(64, 59, 9), new Link(64, 57, 1),
            new Link(64, 11, 1), new Link(65, 63, 5), new Link(65, 64, 7),
            new Link(65, 48, 3), new Link(65, 62, 5), new Link(65, 58, 5),
            new Link(65, 61, 5), new Link(65, 60, 2), new Link(65, 59, 5),
            new Link(65, 57, 1), new Link(65, 55, 2), new Link(66, 64, 3),
            new Link(66, 58, 3), new Link(66, 59, 1), new Link(66, 62, 2),
            new Link(66, 65, 2), new Link(66, 48, 1), new Link(66, 63, 1),
            new Link(66, 61, 1), new Link(66, 60, 1), new Link(67, 57, 3),
            new Link(68, 25, 5), new Link(68, 11, 1), new Link(68, 24, 1),
            new Link(68, 27, 1), new Link(68, 48, 1), new Link(68, 41, 1),
            new Link(69, 25, 6), new Link(69, 68, 6), new Link(69, 11, 1),
            new Link(69, 24, 1), new Link(69, 27, 2), new Link(69, 48, 1),
            new Link(69, 41, 1), new Link(70, 25, 4), new Link(70, 69, 4),
            new Link(70, 68, 4), new Link(70, 11, 1), new Link(70, 24, 1),
            new Link(70, 27, 1), new Link(70, 41, 1), new Link(70, 58, 1),
            new Link(71, 27, 1), new Link(71, 69, 2), new Link(71, 68, 2),
            new Link(71, 70, 2), new Link(71, 11, 1), new Link(71, 48, 1),
            new Link(71, 41, 1), new Link(71, 25, 1), new Link(72, 26, 2),
            new Link(72, 27, 1), new Link(72, 11, 1), new Link(73, 48, 2),
            new Link(74, 48, 2), new Link(74, 73, 3), new Link(75, 69, 3),
            new Link(75, 68, 3), new Link(75, 25, 3), new Link(75, 48, 1),
            new Link(75, 41, 1), new Link(75, 70, 1), new Link(75, 71, 1),
            new Link(76, 64, 1), new Link(76, 65, 1), new Link(76, 66, 1),
            new Link(76, 63, 1), new Link(76, 62, 1), new Link(76, 48, 1),
            new Link(76, 58, 1) };

}
