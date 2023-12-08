package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Create;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Column(name = "name")
    private String name;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Column(name = "description")
    private String description;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Column(name = "available")
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER) //вернуть LAZY, но чтобы тесты по букингам не падали
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;
}