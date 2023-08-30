package com.study.board.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") //localhost:8080/board/write
    public String boardWriteFrom() {

        return "boardwrite";
    }

    @PostMapping("/board/writePro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료 되었습니다.");
//        model.addAttribute("message", "글 작성이 실패했습니다..");
        model.addAttribute("searchUrl", "/board/list");

        return"message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        }else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

//        model.addAttribute("list", boardService.boardList(pageable));

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);



        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id){

        model.addAttribute("board", boardService.boardView(id));

        return"boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {

        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,
                              Model model) {

        model.addAttribute("board", boardService.boardView(id));


        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        // 파일이 null이 아닌 경우에만 파일 업로드 처리
        if (file != null) {
            boardService.write(boardTemp, file);
        } else {
            // 파일이 null인 경우에는 파일을 업로드하지 않고 기존의 내용만 업데이트
            boardService.update(boardTemp);
        }

        model.addAttribute("message", "글이 수정 되었습니다.");     
        model.addAttribute("searchUrl", "/board/list");  

        return "message";   
    }

//    @PostMapping("/board/update/{id}")
//    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{
//
//        Board boardTemp =  boardService.boardView(id);
//        boardTemp.setTitle(board.getTitle());
//        boardTemp.setContent(board.getContent());
//
//        boardService.write(boardTemp, file);
//
//        model.addAttribute("message", "글이 수정 되었습니다.");
//        model.addAttribute("searchUrl", "/board/list");
//
//
//        return "message";
//    }
}
