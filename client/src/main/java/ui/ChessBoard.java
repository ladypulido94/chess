package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final String LIGHT_SQUARE = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE = SET_BG_COLOR_DARK_GREEN;
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY;

    public static void drawWhiteBoard(ChessGame game){
        drawWhiteBoard(game, null);
    }

    public static void drawWhiteBoard(ChessGame game, Collection<ChessMove> moves){
        boolean whiteAtBottom = true;
        drawColumnHeaders(whiteAtBottom);

        for (int i = 8; i >= 1; i--){
            drawRow(i, game, whiteAtBottom, moves);
        }
        drawColumnHeaders(whiteAtBottom);
    }

    public static void drawBlackBoard(ChessGame game){
        drawBlackBoard(game, null);
    }

    public static void drawBlackBoard(ChessGame game, Collection<ChessMove> moves){
        boolean whiteAtBottom = false;
        drawColumnHeaders(whiteAtBottom);

        for(int i = 1; i <= 8; i++){
            drawRow(i, game, whiteAtBottom, moves);
        }

        drawColumnHeaders(whiteAtBottom);

    }

    // ---------- HELPER METHODS -----------

    private static void drawColumnHeaders(boolean whiteAtBottom){
        System.out.print(BORDER_COLOR + "   ");

        if(whiteAtBottom){
            for (char col = 'a'; col <= 'h'; col++){
                System.out.print(" " + col + " ");
            }
        } else {
            for(char col = 'h'; col >= 'a'; col--){
                System.out.print(" " + col + " ");
            }
        }

        System.out.println(RESET_BG_COLOR);
    }

    private static void drawRow(int row, ChessGame game, boolean whiteAtBottom, Collection<ChessMove> moves){
        System.out.print(BORDER_COLOR + " " + row + " " + RESET_BG_COLOR);

        if(whiteAtBottom){
            for(int col = 1; col <= 8; col++){
                String squareColor = getSquareColor(row, col);
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(position);
                String pieceSymbol = getPieceSymbol(piece);

                System.out.print(squareColor + pieceSymbol + RESET_BG_COLOR);

            }
        } else {
            for(int col = 8; col >= 1; col--){
                String squareColor = getSquareColor(row, col);
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(position);
                String pieceSymbol = getPieceSymbol(piece);

                System.out.print(squareColor + pieceSymbol + RESET_BG_COLOR);

            }
        }

        System.out.print(BORDER_COLOR + " " + row + " " + RESET_BG_COLOR);
        System.out.println();
    }

    private static String getSquareColor(int row, int col){
        if(((row + col) % 2) == 0){
            return DARK_SQUARE;
        }

        return LIGHT_SQUARE;
    }

    private static String getPieceSymbol(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        ChessPiece.PieceType pieceType = piece.getPieceType();

        if(pieceColor == ChessGame.TeamColor.WHITE){
            switch (pieceType){
                case ROOK: return WHITE_ROOK;
                case KNIGHT: return WHITE_KNIGHT;
                case BISHOP: return WHITE_BISHOP;
                case KING: return WHITE_KING;
                case QUEEN: return WHITE_QUEEN;
                case PAWN: return WHITE_PAWN;
            }
        } else {
            switch (pieceType){
                case ROOK: return BLACK_ROOK;
                case KNIGHT: return BLACK_KNIGHT;
                case BISHOP: return BLACK_BISHOP;
                case KING: return BLACK_KING;
                case QUEEN: return BLACK_QUEEN;
                case PAWN: return BLACK_PAWN;
            }
        }

        return EMPTY;
    }
}
