package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class Master
{
    public ArrayList<Slave> slaves = new ArrayList<>();//id потока может не соответствоватьт с позицией в slaves
    public ArrayList<JsonHandler> tasks = new ArrayList<>();
    private boolean is_active;

    Master()
    {
        is_active = true;
    }

    synchronized void update_tasks()
    {
        try
        {
            JsonHandler[] new_tasks = take_json_from_url();
            if (new_tasks.length != 0)
            {
                for (int i = 0; i < new_tasks.length; i++)
                {
                    tasks.add(new_tasks[i]);
                    System.out.println(new_tasks[i].all_to_string());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Tasks не обновлены");
        }
    }

    JsonHandler[] take_json_from_url()
    {
        StringBuilder json = new StringBuilder();
        String line;
        String url_string = "http://194.67.91.76/index.php";
        try
        {
            URL url = new URL(url_string);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null)
            {
                json.append(line);
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        JsonParser json_parser = new JsonParser();
        return json_parser.get_all_JsonHandlers(json.toString());
    }

    void update_slave(int slave_id) throws InterruptedException//получает slave_id,который стригерил запрос
    {
        int thread_id;
        boolean task_founded = false;
        thread_id = slaves.get(slave_id).taskJson.id;

        if (tasks.size() != 0)
        {
            for (int i = 0; i < tasks.size(); i++)//если в тасках нет thread_id(который стригерил запрос),то удаляем поток
            {
                if (tasks.get(i).id == thread_id)//если в тасках нашли задачу для thread_id,то меняем ему задачу
                {
                    slaves.get(slave_id).taskJson = tasks.get(i);
                    tasks.remove(i);
                    slaves.get(slave_id).set_unfinished();
                    task_founded = true;
                    break;
                }
            }
            if (task_founded == false)//если задача не найдена,то удаляем этот поток
            {
                kill_thread_by_slave_id(slave_id);
            }
        }
        else
        {
            kill_thread_by_slave_id(slave_id);
        }
    }

    void create_slaves_from_tasks()//создаем новые потоки из задач
    {
        if (tasks.size() != 0)
        {
            for (int i = 0; i < tasks.size(); i++)
            {
                if (get_slave_id_by_thread_id(tasks.get(i).id) != -1)
                {
                    continue;
                }
                else
                {
                    create_thread(tasks.get(i));
                    tasks.remove(i);
                    i--;
                }
            }
        }
    }

    void create_thread(JsonHandler taskJson)
    {
        Slave newSlave = new Slave(taskJson);
        slaves.add(newSlave);
        newSlave.start();
    }

    boolean get_active()
    {
        return is_active;
    }

    void disable()
    {
        is_active = false;
    }

    int get_slave_id_by_thread_id(int thread_id)//если не нашли раба по id потока возвращаем -1
    {
        if (slaves.size() != 0)
        {
            for (int i = 0; i < slaves.size(); i++)
            {
                if (slaves.get(i).taskJson.id == thread_id)
                    return i;
            }
        }
        return -1;
    }

    void kill_thread_by_slave_id(int slave_id) throws InterruptedException
    {
        slaves.get(slave_id).disable();
        slaves.get(slave_id).set_unfinished();
        slaves.remove(slave_id);
    }

    void kill_thread_by_thread_id(int thread_id) throws InterruptedException
    {
        int slave_id;
        slave_id = get_slave_id_by_thread_id(thread_id);
        if (slave_id != -1)
        {
            slaves.get(slave_id).disable();
            slaves.get(slave_id).set_unfinished();
            slaves.remove(slave_id);
        }
    }

    void kill_all_threads() throws InterruptedException//потоки заканчивают текущий цикл while и удаляются
    {
        if (slaves.size() != 0)
        {
            for (int i = 0; i < slaves.size(); i++)
            {
                slaves.get(i).disable();
                slaves.get(i).set_unfinished();
            }
            slaves.removeAll(slaves);
        }
    }

}
