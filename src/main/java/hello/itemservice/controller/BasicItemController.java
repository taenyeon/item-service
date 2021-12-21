package hello.itemservice.controller;

import hello.itemservice.domain.item.Item;
import hello.itemservice.repository.item.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
//@RequiredArgsConstructor 어노테이션을 사용하면 생성자를 생략하는것도 가능하다.
public class BasicItemController {
    private final ItemRepository itemRepository;

    @Autowired
    // 스프링 빈에 등록된 리포지토리를 불러와 해당 클레스에 적용
    public BasicItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    //    @PostMapping("/add")
    // @RequestParam을 사용하면 아래와 같이 값을 보내게 된다.
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item", item);

        return "basic/item";
    }

    //    @PostMapping("/add")
    // @modelAttribute를 사용하면 이미 정의한 객체를 손쉽게 넣을 수 있다.
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);

        return "basic/item";
    }

    //    @PostMapping("/add")
    // @modelAttribute를 사용하면 이미 정의한 객체를 손쉽게 넣을 수 있다.
    // V2에서 더 간략화 하기위해선 Item이라는 객체의 첫 글자를 소문자로
    // 적용하여 인자를 생략할 수 있다.(Item -> item)
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);

        return "basic/item";
    }

    //    @PostMapping("/add")
    // @modelAttribute까지도 생략이 가능하다.
    // 이를 생략하면 자동으로 Item 객체를 @modelAttribute로 보내는 작업까지
    // Spring에서 해주기때문에 이렇게 넣을 수 있지만,
    // 이렇게 되면 요청을 통해 받은 변수인지 혹은 일반적인 변수인지 헷갈리는 경우도 많기 때문에
    // 지양하는 경우가 많다.
    public String addItemV4(Item item) {
        itemRepository.save(item);

        return "basic/item";
    }

//    @PostMapping("/add")
    // redirect를 통해서 새로고침시 계속 Post 요청이 들어가는것을 막는다.
    public String addItemV5(Item item) {
        itemRepository.save(item);

        return "redirect:/basic/items/"+item.getId();
    }

    @PostMapping("/add")
    // redirect를 통해서 새로고침시 계속 Post 요청이 들어가는것을 막는다.
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        redirectAttributes.addAttribute("status",true);
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }


    // 테스트용 데이터 추가
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }


}