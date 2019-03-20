package ru.hse.spb.sd.sharkova.interpreter


val file1Lines = listOf("text", "line", "", "the previous line was an empty one").map { stringWithNewline(it) }
val file2Lines = listOf("So long as there shall exist, by virtue of law and custom, " +
        "decrees of damnation pronounced by society, artificially creating hells amid the civilization of earth, " +
        "and adding the element of human fate to divine destiny; " +
        "so long as the three great problems of the century—the degradation of man through pauperism, " +
        "the corruption of woman through hunger, the crippling of children through lack of light—are unsolved; " +
        "so long as social asphyxia is possible in any part of the world;—in other words, " +
        "and with a still wider significance, so long as ignorance and poverty exist on earth, " +
        "books of the nature of Les Misérables cannot fail to be of use.")
const val file1Wc = "4 9 46"
const val file2Wc = "0 109 651"


fun stringWithNewline(string: String) = string + System.lineSeparator()