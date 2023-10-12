package br.com.viniciusborba.tododolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.viniciusborba.tododolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;
    
  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest req) {
    var userId = req.getAttribute("userId");
    taskModel.setUserId((UUID) userId);
    
    var currentDate = LocalDateTime.now();

    if (currentDate.isAfter(taskModel.getStartAt())) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("The start date must be greater than current date!");
    }

    if (currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("The end date must be greater than current date!");
    }

    if ((taskModel.getStartAt()).isAfter(taskModel.getEndAt())) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("The start date can not be greater than end date!");
    }

    var task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest req) {
    var userId = req.getAttribute("userId");
    var tasks = this.taskRepository.findManyByUserId((UUID) userId);

    return tasks;
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest req) {
    var task = this.taskRepository.findById(id).orElse(null);

    Utils.copyNonNullProperties(taskModel, task);

    return this.taskRepository.save(task);
  }
}
