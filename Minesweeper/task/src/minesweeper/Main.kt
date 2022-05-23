package minesweeper

import kotlin.random.Random

class Game(){
    val board = Board()
    var status = true
    var foundMines = 0
    var nbMines = 0
    fun playMineSweeper(){
        board.displayBoard()
        var checked = false
        while (!checked){
            println("How many mines do you want on the field?")
            nbMines = readLine()!!.toInt()
            if (nbMines <= board.nbRows * board.nbCols) checked = true
        }
        board.setMines(nbMines)
        board.displayBoard()
        while (status){
            println("Set/unset mine marks or claim a cell as free:")
            val command = readLine()!!.split(" ")
            val cCol = command.first().toInt()
            val cRow = command[1].toInt()
            when (command.last()){
                "mine"  -> flagCell(cRow, cCol)
                "free"  -> freeCell(cRow, cCol)
                else    -> continue
            }
            board.displayBoard()
            println("Nb of mines found: $foundMines/$nbMines")
        }
    }
    fun freeCell(row: Int, col: Int){
        if (board.playBoard[row][col].mined){
            for (r in 1..9){
                for (c in 1..9){
                    if (board.playBoard[r][c].mined) board.playBoard[r][c].shown = 'X'
                }
            }
            status = false
            println("You stepped on a mine and failed!")
        }
        else {
            board.playBoard[row][col].freed = true

            if (board.playBoard[row][col].neighbors == 0){
                board.playBoard[row][col].shown = '/'
                lookAround(row, col)
            } else {
                board.playBoard[row][col].shown = board.playBoard[row][col].neighbors.toChar() + '0'.code
            }
        }
    }
    fun flagCell(row: Int, col: Int){
        if (board.playBoard[row][col].flagged){         //if already flagged, unflag it
            board.playBoard[row][col].flagged = false
            board.playBoard[row][col].shown = '.'
            if (board.playBoard[row][col].mined) foundMines--
        } else {                                        //else flag it
            board.playBoard[row][col].flagged = true
            board.playBoard[row][col].shown = '*'
            if (board.playBoard[row][col].mined) foundMines++
        }
        if (foundMines == nbMines) {
            println("Congratulations! You found all the mines!")
            status = false
        }
    }
    fun lookAround(row: Int, col: Int){
        if (row in 1..9 && col in 1..9) {
            for (r in -1..1) {
                for (c in -1..1) {
                    if (/*!board.playBoard[row + r][col + c].flagged &&*/ !board.playBoard[row + r][col + c].freed) {
                        board.playBoard[row + r][col + c].freed = true
                        if (board.playBoard[row + r][col + c].neighbors == 0){
                            board.playBoard[row + r][col + c].shown = '/'
                            lookAround(row + r, col + c)
                        } else {
                            board.playBoard[row + r][col + c].shown = board.playBoard[row + r][col + c].neighbors.toChar() + '0'.code
                        }
                    }
                }
            }
        }
    }

}
class Cell(){
    var shown = '.'
    var neighbors = 0
    var mined = false
    var flagged = false
    var freed = false
}
class Board(){
    val nbRows = 9
    val nbCols = 9
    val playBoard = List(nbRows + 2){ List(nbCols + 2) {Cell()} }
    fun displayBoard(){
        for(r in 0..nbRows){
            if(r == 0) println(" |${(1..nbCols).toList().joinToString("")}|\n" +
                    "-|${"-".repeat(nbCols)}|")
            else {
                print("$r|")
                for (c in 1..nbCols) {
                    print(playBoard[r][c].shown)
                }
                print("|\n")
            }
        }
        println("-|${"-".repeat(nbCols)}|\n")
    }
    fun setMines(nbMines: Int){
        val mineList: MutableSet<Int> = mutableSetOf<Int>()
        // Here is where the mines are laid
        while(mineList.size < nbMines){
            val maxPosition = nbRows * 10 + nbCols
            val position = Random.nextInt(11, maxPosition)
            if (position !in mineList) {
                mineList.add(position)
                var row = position.floorDiv(10)
                var col = position - row * 10
                playBoard[row][col].mined = true
            }
        }
        println(mineList)
        // Here is where the nb of mines in neighbourhood is updated
        for (r in 1..nbRows){
            for (c in 1..nbCols){
                if (playBoard[r][c].mined) for(i in -1..1) for (j in -1..1) playBoard[r + i][c + j].neighbors++
            }
        }
    }

}

fun main() {
    val game = Game()
    game.playMineSweeper()
}