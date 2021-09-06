document.addEventListener("DOMContentLoaded", function(event) {


  // view of pencil an trash bin near the task
  Vue.component('todo-item', {
    props: ['todo'],
    template: `<li>
      <span v-on:click="$emit('deleted', todo.id)">
        <i class="fas fa-trash-alt"></i>
      </span><span v-on:click="$emit('edit', todo.id)">
        <i id="pencil" class="fas fa-pencil-alt"></i>
      </span>{{todo.text}}</li>`
  })

  var app = new Vue({
    //looks like shema for parsing json data
    el: '#todo-app',
    data: {
      todos: [],
      todoText: '',
      editingItemId: null
    },
    created: function() {
      this.loadTodos()
    },
    methods: {
      //get all items
      loadTodos: function() {
        var _this = this;
        axios.get('/todo')
          .then(resp => _this.todos = _this.todos.concat(resp.data))
          .catch(error => console.log(error));
      },
      editItem: function(itemId) {
        this.editingItemId = itemId;
        this.todoText = this.todos.find(i => i.id == itemId).text;
        this.$refs.todoTextInput.focus();
      },
      clearTodoText: function() {
        this.todoText = ''
        this.editingItemId = null;
      },
      //delete item by id
      deleteItem: function(itemId) {
        var _this = this;
        axios.delete('/todo', { params: { id: itemId } })
          .then(function(resp) {
            _this.todos = _this.todos.filter(i => i.id != itemId);
          })
          .catch(error => console.log(error));
      },
      //sending or replacing task
      saveItem: function() {
        if (this.todoText) {
          var _this = this;
          axios.post('/todo',
              {id: _this.editingItemId, text: _this.todoText}
          )
              .then(function (resp) {
                if (_this.editingItemId) {
                  _this.todos = _this.todos.filter(i => i.id != _this.editingItemId);
                }
                _this.todos.push(resp.data);
                _this.clearTodoText()
              })
              .catch(error => console.log(error));
        }
      },

      //deleting all items
      clear: function () {
        var _this = this;
        axios.delete('/todo')
            .then(function (resp) {
              _this.todos = []
            })
            .catch(error => console.log(error));
      }
    }
  })
});
