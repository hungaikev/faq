## Sinks

```Sink``` - The receiver of the date after its transformed by the ```Flow``` consists of exactly one input. It is the subscriber of the data
sent/processed by a ```Source```. Usually it outputs its input to some system IO( TCP port, console, file, etc) Creating the side effect
of our application working. It is basically a ```Flow``` which uses a ```foreach``` or ```fold``` function to run a procedure(function with 
no return value, Unit) over its input elements and propagate the auxiliary value(eg a ```Future``` that will complete when it finishes writing the input
to a file or console)