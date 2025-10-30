#!/usr/bin/env python3
import sys, random

def load_program(path):
    lines = [list(l.rstrip('\n')) for l in open(path)]
    width = max(map(len, lines))
    return [l + [' '] * (width - len(l)) for l in lines]

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 befunge.py <file.bf>")
        sys.exit(1)

    grid = load_program(sys.argv[1])
    x = y = 0
    dx, dy = 1, 0
    stack = []
    string_mode = False
    h, w = len(grid), len(grid[0])

    def pop(): return stack.pop() if stack else 0

    while True:
        c = grid[y][x]
        if string_mode:
            if c == '"':
                string_mode = False
            else:
                stack.append(ord(c))
        else:
            if c.isdigit():
                stack.append(int(c))
            elif c == '+': a,b=pop(),pop(); stack.append(b+a)
            elif c == '-': a,b=pop(),pop(); stack.append(b-a)
            elif c == '*': a,b=pop(),pop(); stack.append(b*a)
            elif c == '/': a,b=pop(),pop(); stack.append(b//a if a else 0)
            elif c == '%': a,b=pop(),pop(); stack.append(b%a if a else 0)
            elif c == '!': stack.append(0 if pop() else 1)
            elif c == '`': a,b=pop(),pop(); stack.append(1 if b>a else 0)
            elif c == '>': dx,dy=1,0
            elif c == '<': dx,dy=-1,0
            elif c == '^': dx,dy=0,-1
            elif c == 'v': dx,dy=0,1
            elif c == '?': dx,dy=random.choice([(1,0),(-1,0),(0,1),(0,-1)])
            elif c == '_': dx,dy=(1,0) if pop()==0 else (-1,0)
            elif c == '|': dx,dy=(0,1) if pop()==0 else (0,-1)
            elif c == '"': string_mode=True
            elif c == ':': a=pop(); stack += [a,a]
            elif c == '\\': a,b=pop(),pop(); stack += [a,b]
            elif c == '$': pop()
            elif c == '.': print(pop(), end=' ')
            elif c == ',': print(chr(pop()), end='')
            elif c == '#': x = (x+dx) % w; y = (y+dy) % h
            elif c == '@': break
        x = (x + dx) % w
        y = (y + dy) % h

if __name__ == "__main__":
    main()
