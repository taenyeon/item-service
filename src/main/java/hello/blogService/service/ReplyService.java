package hello.blogService.service;

import hello.blogService.config.DateSet;
import hello.blogService.domain.Reply;
import hello.blogService.repository.ReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Service
public class ReplyService {
    private final ReplyRepository replyRepository;

    public ReplyService(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }
    public List<Reply> findById(String boardId){
        return replyRepository.findByBoardId(boardId);
    }


    public int addReply(Reply reply){
        reply.setReplyWriteDate(DateSet.getNow());
        if (reply.getReplyParentId() == 0){
            int order = replyRepository.findMaxReplyOrderByBoardId(String.valueOf(reply.getBoardId()))+1;
            reply.setReplyOrder(order);
        }
        else {
            System.out.println(reply.getReplyParentId());
            // 부모 댓글 꺼내옴
            Reply parentReply = replyRepository.findByReplyId(String.valueOf(reply.getReplyParentId()));
            // 부모 댓글의 부모 번호 저장 (삭제시 동시 삭제 가능)
            reply.setReplyParentId(parentReply.getReplyId());
            // 부모 댓글의 깊이 +1 저장 (부모 댓글의 아래에 존재하는 댓글임을 의미)
            reply.setReplyDepth(parentReply.getReplyDepth()+1);
            // 부모 댓글의 순서 +1 저장 (자식 댓글이 부모 댓글의 바로 아래에 올 수 있도록 함.)
            reply.setReplyOrder(parentReply.getReplyOrder()+1);
            replyRepository.updateReplyOrder(String.valueOf(parentReply.getReplyOrder()));
        }
        return replyRepository.insertReply(reply);
    }

    public int modifyReply(Reply reply){
        reply.setReplyModifiedDate(DateSet.getNow());
        return replyRepository.updateReply(reply);

    }

    public int deleteReply(String replyId){
        return replyRepository.deleteReply(replyId);
    }
}
