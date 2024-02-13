package com.levik.bibernate.demo.ddl;

import io.github.blyznytsiaorg.bibernate.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "phone_idx", columnList = "companyNumber")})
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq",
            initialValue = 10, allocationSize = 20)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String mobileNumber;
    private String companyNumber;
    @ManyToOne
    @JoinColumn(name = "author_profile_id", foreignKey = @ForeignKey(name = "FK_phone_author_profile"))
    private AuthorProfile authorProfile;
}
