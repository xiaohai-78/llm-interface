import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamTest {

    @Test
    public void testPickElementByIndex() {
        List<String> list = Arrays.asList("Apple", "Banana", "Cherry", "Date", "Elderberry");

        // 选择索引为 2 的元素（即 "Cherry"）
        Optional<String> pickedElement = pickElementByIndex(list.stream(), 2);

        // 验证结果
        assertTrue(pickedElement.isPresent());
        assertEquals("Cherry", pickedElement.get());
    }

    private <T> Optional<T> pickElementByIndex(Stream<T> stream, int index) {
        return stream.peek(System.out::println).skip(index).findFirst();
    }

    private List<Student> getStudents() {
        Student s1 = new Student("xiaoli", 18, 95);
        Student s2 = new Student("xiaoming", 21, 100);
        Student s3 = new Student("xiaohua", 19, 98);
        List<Student> studentList = Lists.newArrayList();
        studentList.add(s1);
        studentList.add(s2);
        studentList.add(s3);
        return studentList;
    }

    public void refactorBefore() {
        List<Student> students = getStudents();
        students.stream().peek(student -> {
            student.setAge(student.getAge() + 1);
        });
        System.out.println(students);
    }

    @Test
    public void refactorAfter() {
        refactorBefore();
    }

    @Getter
    @Setter
    public class Student {
        private String name;
        private int age;
        private int score;

        // Constructor
        public Student(String name, int age, int score) {
            this.name = name;
            this.age = age;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Student{name='" + name + "', age=" + age + "}";
        }
    }
}
