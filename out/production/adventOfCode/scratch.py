with open("input.in") as f:
    fresh, shelf = f.read().split("\n\n")
    shelf = [int(num) for num in shelf.splitlines()]
    fresh = {(int(l), int(r)) for l, r in [
        line.split("-") for line in fresh.splitlines()
    ]}

print(len(fresh))
print(len(shelf))

total = 0
for item in shelf:
    for l, r in fresh:
        if l <= item <= r:
            total += 1
            break

print(total)
