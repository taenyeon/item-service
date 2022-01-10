package hello.itemService.service;

import hello.itemService.domain.Member;
import hello.itemService.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class MemberService {
    @Value("${spring.servlet.multipart.location}")
    String path;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 비밀번호 암호화 메소드
    private Member passwordEncoding(Member member){
        String encodedPwd = passwordEncoder.encode(member.getPwd());
        member.setPwd(encodedPwd);
        return member;
    }

    /**
     * 회원가입
     */
    public String join(Member member) {
        // 중복 X
        validateDuplicateMember(member); // 중복 회원 검증
        Member encodedMember = passwordEncoding(member);
        if (memberRepository.save(encodedMember) != 0) {
            return member.getId();
        } else {
            throw new IllegalStateException("DB 오류로 회원 가입에 실패하였습니다.");
        }
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findById(member.getId()).
                ifPresent(isMember -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 조회
     */
    public Optional<Member> findMember(String memberId) {
        return memberRepository.findById(memberId);
    }

    /**
     * 회원 로그인
     */
    public Optional<Member> loginMember(Member member) {
        Optional<Member> loginMember = memberRepository.findById(member.getId());
        if (loginMember.isPresent()) {
            // passwordEncoder 의 match() 메소드를 통해 암호화한 비밀번호와 입력한 비밀번호 비교
            if (passwordEncoder.matches(member.getPwd(),loginMember.get().getPwd())){
            return loginMember;
            } else {
                throw new IllegalStateException("비밀번호 오류입니다.");
            }
        } else {
            throw new IllegalStateException("아이디 오류입니다.");
        }
    }

    public String updateMember(Member member, MultipartFile file) throws IOException {
            String changedName = member.getId()+".jpg";
        if (!file.isEmpty()) {
            File f = new File(path + "/member/" + changedName);
            file.transferTo(f);
            member.setImg(changedName);
            System.out.println("OK");
        }
        int result = memberRepository.update(member);
        if (result != 0) {
            return member.getImg();
        } else {
            throw new IllegalStateException("업데이트에 실패하였습니다.");
        }
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
