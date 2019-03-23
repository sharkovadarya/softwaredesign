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
val file3Lines = listOf("since running 'gradle test' on windows " +
        "somehow messes up the encoding and i couldn't find a way to fix it, " +
        "i'll have to use another overly long string without a line separator instead. " +
        "by the way, if you run the test in intellij idea, without using gradle, " +
        "everything works, but gradle somehow messes up diacritic characters in string, " +
        "i'm not sure why. what a pity. anyway.")
const val file1LineCount = 4
const val file1WordCount = 9
val file1ByteCount = 42 + file1LineCount * System.lineSeparator().length
val file1Wc = "$file1LineCount $file1WordCount $file1ByteCount"
const val file2LineCount = 0
const val file2WordCount = 109
const val file2ByteCount = 651
const val file2Wc = "$file2LineCount $file2WordCount $file2ByteCount"
const val file3LineCount = 0
const val file3WordCount = 66
const val file3ByteCount = 374
const val file3Wc = "$file3LineCount $file3WordCount $file3ByteCount"
val totalWc12 = "${file1LineCount + file2LineCount} " +
                "${file1WordCount + file2WordCount} " +
                "${file1ByteCount + file2ByteCount}"
val totalWc13 = "${file1LineCount + file3LineCount} " +
        "${file1WordCount + file3WordCount} " +
        "${file1ByteCount + file3ByteCount}"



fun stringWithNewline(string: String) = string + System.lineSeparator()

fun listStringsWithNewlines(lines: List<String>) = lines.map { stringWithNewline(it) }